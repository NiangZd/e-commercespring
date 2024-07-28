package com.projetosapatos.demo.controllers;

import com.projetosapatos.demo.aplicacao.Sapato;
import com.projetosapatos.demo.repository.SapatoRepository;
import com.projetosapatos.demo.service.SapatoService;
import com.projetosapatos.demo.service.FileStorageService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class SapatoController {

    @Autowired
    private SapatoRepository sapatorep;

    private final SapatoService service;

    public SapatoController(SapatoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ModelAndView listar(HttpServletResponse response, Model model, HttpSession session) {
        List<Sapato> todosSapatos = sapatorep.findAll();
        List<Sapato> sapatosVisiveis = todosSapatos.stream()
            .filter(sapato -> sapato.isDeleted() == null)
            .collect(Collectors.toList());

        ModelAndView mv = new ModelAndView("index");
        mv.addObject("listaSapatos", sapatosVisiveis);

        // PEGA O CARRINHO DA SESSÃO ATUAL
        @SuppressWarnings("unchecked")
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }
        mv.addObject("carrinho", carrinho);

        // COOKIE COM DATA E HORA
        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        Cookie cookie = new Cookie("visita", dataHora);
        cookie.setMaxAge(24 * 60 * 60); // COLOCANDO 24 HORAS PRO COOKIE
        response.addCookie(cookie);

        return mv;
    }

    @GetMapping("/admin")
    public ModelAndView listarAdm(Model model) {
        ModelAndView mv = new ModelAndView("adm/paineladm");
        mv.addObject("listaSapatos", sapatorep.findAll());
        
        if (model.containsAttribute("message")) {
            mv.addObject("message", model.getAttribute("message"));
        }

        return mv;
    }

    @GetMapping("/cadastroPage")
    public String getCadastroPage(Model model) {
        Sapato sapato = new Sapato();
        model.addAttribute("sapato", sapato);
        return "cadastroPage";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute @Valid Sapato sapato, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // VERIFICAÇÃO DOS ID's
            if (sapato.getId() == null) {
                // SE NÃO TIVER ID, CADASTRA
                return "cadastroPage";
            } else {
                // SE TIVER ID, EDITA
                return "editPage";
            }
        }

        if (sapato.getId() == null) {
            service.create(sapato);
            redirectAttributes.addFlashAttribute("message", "Sapato cadastrado com sucesso!");
        } else {
            service.update(sapato);
            redirectAttributes.addFlashAttribute("message", "Sapato atualizado com sucesso!");
        }

        return "redirect:/admin";
    }

    @GetMapping("/editPage/{id}")
    public ModelAndView editPage(@PathVariable String id) {
        Optional<Sapato> sapato = service.findById(id);

        if (!sapato.isPresent()) {
            return new ModelAndView("redirect:/admin");
        }

        ModelAndView modelAndView = new ModelAndView("editPage");
        modelAndView.addObject("sapato", sapato.get());

        return modelAndView;
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "Deletado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao deletar sapato: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam("id") String id, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<Sapato> sapatoOpt = sapatorep.findById(id);

        if (sapatoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Sapato não encontrado.");
            return "redirect:/";
        }

        Sapato sapato = sapatoOpt.get();
        
        // CRIA UM CARRINHO, OU PEGA O ATUAL DA SESSÃO SE TIVER UM
        @SuppressWarnings("unchecked")
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
        }

        carrinho.add(sapato);
        session.setAttribute("carrinho", carrinho);

        return "redirect:/";
    }

    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        // RECUPERAÇÃO DO CARRINHO DA SESSÃO ATUAL
        @SuppressWarnings("unchecked")
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");
        
        if (carrinho == null || carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Não existem itens no carrinho.");
            return "redirect:/";
        }

        // VISUALIZAÇÃO DO CARRINHO COM ADD
        model.addAttribute("carrinho", carrinho);

        // FUNÇÃO PARA CALCULAR O VALOR DO CARRINHO
        double carrinhoTotal = carrinho.stream()
                                    .mapToDouble(Sapato::getPreco)
                                    .sum();
        model.addAttribute("carrinhoTotal", carrinhoTotal);

        return "verCarrinho";
    }

    @PostMapping("/removerItem")
    public String removerItem(@RequestParam("carrinhoId") String id, HttpSession session, RedirectAttributes redirectAttributes) {
        // PEGA O CARRINHO DA SESSÃO, SE TIVER, OU CRIA UM NOVO SE NECESSÁRIO
        @SuppressWarnings("unchecked")
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");
        
        if (carrinho != null) {
            // REMOÇÃO
            carrinho.removeIf(sapato -> sapato.getId().equals(id));
            session.setAttribute("carrinho", carrinho);

            redirectAttributes.addFlashAttribute("mensagem", "Item removido com sucesso.");
        }

        return "redirect:/verCarrinho";
    }

    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");

        double carrinhoTotal = carrinho != null ? carrinho.stream().mapToDouble(Sapato::getPreco).sum() : 0;

        session.removeAttribute("carrinho");

        redirectAttributes.addFlashAttribute("mensagem", "Compra finalizada! Valor total: R$ " + carrinhoTotal);

        return "redirect:/";
    }
}