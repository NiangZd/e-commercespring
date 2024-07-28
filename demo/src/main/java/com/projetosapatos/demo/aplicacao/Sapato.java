package com.projetosapatos.demo.aplicacao;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date; 

import org.hibernate.validator.constraints.URL;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sapato")
@Data
public class Sapato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O nome do sapato é obrigatório.")
    private String nome;

    @NotNull(message = "O preço é obrigatório.")
    @Positive(message = "O preço deve ser positivo.")
    private Double preco;

    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 500, message = "A descrição não pode exceder 500 caracteres.")
    private String descricao;

    @NotNull(message = "O tamanho é obrigatório.")
    @Min(value = 20, message = "O tamanho deve ser no mínimo 20.")
    @Max(value = 50, message = "O tamanho deve ser no máximo 50.")
    private Integer tamanho;

    @NotBlank(message = "A marca é obrigatória.")
    private String marca;

    @NotBlank(message = "A URI da imagem é obrigatória.")
    @URL(message = "A URI da imagem deve ser uma URL válida.")
    @Column(name = "imageUri", nullable = false)
    private String imageUri;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column
    private Date isDeletedNew = null;

    public Sapato(String nome, Double preco, String descricao, Integer tamanho, String marca, String imageUri) {
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.marca = marca;
        this.imageUri = imageUri;
    }   

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date isDeleted() {
        return isDeletedNew;
    }

    public void setIsDeletedNew(Date isDeletedNew){
        this.isDeletedNew = isDeletedNew;
    }
}
