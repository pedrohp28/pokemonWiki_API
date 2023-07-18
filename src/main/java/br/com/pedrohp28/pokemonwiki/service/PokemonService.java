package br.com.pedrohp28.pokemonwiki.service;

import br.com.pedrohp28.pokemonwiki.controller.PokemonController;
import br.com.pedrohp28.pokemonwiki.data.vo.PokemonVO;
import br.com.pedrohp28.pokemonwiki.exceptions.ResourceNotFoundException;
import br.com.pedrohp28.pokemonwiki.mapper.EntityMapper;
import br.com.pedrohp28.pokemonwiki.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PokemonService {
    private Logger logger = Logger.getLogger(PokemonService.class.getName());

    @Autowired
    PokemonRepository repository;

    @Autowired
    PagedResourcesAssembler<PokemonVO> assembler;

    public PokemonVO findById(Long id) {
        logger.info("Entrou umn pokemon!");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records founds for this ID!"));
        var vo = EntityMapper.parseObject(entity, PokemonVO.class);
        vo.add(linkTo(methodOn(PokemonController.class).findById(id)).withSelfRel());
        return vo;
    }

    public PagedModel<EntityModel<PokemonVO>> findAll(Pageable pageable) {
        logger.info("Entrou todos os pokemon!");

        var personPage = repository.findAll(pageable);

        var personVosPage = personPage.map(p -> EntityMapper.parseObject(p, PokemonVO.class));
        personVosPage.map(p -> p.add(linkTo(methodOn(PokemonController.class).findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PokemonController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(personVosPage, link);
    }
}
