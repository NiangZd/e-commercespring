package com.projetosapatos.demo.controllers;

import com.projetosapatos.demo.aplicacao.Usuario;
import com.projetosapatos.demo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registrar")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registrar")
    public String registerUser(@ModelAttribute("usuario") @Valid Usuario usuario,
                               BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Erro ao registrar usuário. Verifique os campos e tente novamente.");
            return "registro";
        }

        try {
            usuarioService.salvarUsuario(usuario);
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("errorMessage", "Erro ao registrar usuário. Username ou email já existe.");
            return "registro";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Cadastro realizado com sucesso. Faça o login.");
        return "redirect:/login";
    }

}
