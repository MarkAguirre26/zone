package com.virtual.zone.module.controller;

import com.virtual.zone.module.service.ZoneHelper;
import com.virtual.zone.module.model.DivElement;
import com.virtual.zone.module.model.ZoneResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api")
public class MainController {
    private static final Logger logger = Logger.getLogger(MainController.class.getName());
    private final ZoneHelper zoneHelper;

    public MainController(ZoneHelper zoneHelper) {
        this.zoneHelper = zoneHelper;
    }

    @GetMapping("/process-request")
    public ResponseEntity<List<String>> processEvent(@RequestParam String event, @RequestParam String draft) {
        List<String> zoneData = new ArrayList<>();

        try {
            if (event.contains("P_Click")) {
                event = "Module1." + event;
            }

            logger.info("Event: " + event);
            if (!event.equals("currentState")) {
                logger.info("Triggering macro" + event);
                zoneData = zoneHelper.triggerMacro(event);
            }
            if (event.equals("Reset_Shoe")) {
                //TO DO WRITE
                logger.info("Draft: " + draft);
                logger.info("Reset Shoe");
                zoneHelper.writeMacro(draft);
                logger.info("Triggering macro" + event);
                zoneData = zoneHelper.triggerMacro(event);
            }
            if (event.equals("currentState")) {
                zoneData = zoneHelper.getCurrentStateMacroData();
            }

            return ResponseEntity.ok(zoneData);
        } catch (Exception e) {
            // Log exception
            logger.severe("Error processing request " + e.getMessage());
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }

    }


//    @PostMapping("/cardClicked")
//    public ResponseEntity<List<DivElement>> cardClicked(@RequestParam Map<String, String> param) {
//        try {
//
//            ZoneHelper zoneHelper = new ZoneHelper();
//            List<String> macroData = zoneHelper.getMacroData();
//
//            for (String macroName : macroData) {
//                System.out.println(macroName);
//            }
//
//            return ResponseEntity.ok(null);
//        } catch (Exception e) {
//            // Log exception
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
//    }


}
