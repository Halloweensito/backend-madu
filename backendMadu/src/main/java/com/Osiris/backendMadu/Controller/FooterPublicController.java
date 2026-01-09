package com.Osiris.backendMadu.Controller;

import com.Osiris.backendMadu.DTO.Section.FooterSectionPublic;
import com.Osiris.backendMadu.Service.FooterPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl; // Importante
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/public/footer")
@RequiredArgsConstructor
public class FooterPublicController {

    private final FooterPublicService footerPublicService;

    @GetMapping
    public ResponseEntity<List<FooterSectionPublic>> getFooter() {

        // 1. Obtenemos los datos (Ya optimizados en el servicio)
        var footerData = footerPublicService.getFooter();

        // 2. Agregamos Cache-Control
        // Esto le dice al navegador: "Guarda esto en caché por 1 hora (3600 seg)"
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(footerData);
    }
}