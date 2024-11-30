package com.virtual.zone.module.controller;

import com.virtual.zone.module.model.AppResponse;
import com.virtual.zone.module.model.DivElement;
import com.virtual.zone.module.model.ZoneResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/default")
    public ResponseEntity<AppResponse> getDefault(@RequestParam String draft) {
        try {
            AppResponse appResponse = new AppResponse();
            appResponse.setZoneResponse(getZoneResponseDeafualtValue(draft));

            appResponse.setDivElement(getDivElementsDefaultValue(Integer.parseInt(draft)));
            return ResponseEntity.ok(appResponse);
        } catch (Exception e) {
            // Log exception
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    @PostMapping("/cardClicked")
    public ResponseEntity<List<DivElement>> cardClicked(@RequestParam Map<String, String> param) {
        try {

            String cardId = param.get("cardId");
            List<DivElement> divElements = getDivElements(param);

            if (!updateDivElementContent(divElements, cardId)) {
                return ResponseEntity.badRequest()
                        .body(divElements); // Optionally include the current state of elements
            }

            return ResponseEntity.ok(divElements);
        } catch (Exception e) {
            // Log exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }


    private ZoneResponse getZoneResponseDeafualtValue(String draft) {
        ZoneResponse z = new ZoneResponse();
        z.setShenro("-7.61%");
        z.setLucky("-10.19%");
        z.setCloud("-9.31%");
        z.setSss("-7.78%");
        z.setEverythingUnderTheSun("-2.97%");
        z.setBluesky("-1.24%");
        z.setRedsea("-1.02%");
        z.setEven("-14.4%");
        z.setDraftNumber(draft);
        return z;
    }

    public List<DivElement> getDivElementsDefaultValue(int draft) {
        List<DivElement> divElements = new ArrayList<>();

            int content = draft * 4;
        for (int i = 1; i <= 14; i++) {

            divElements.add(new DivElement("div-" + i, i <= 13 ? content : 128));
        }


        return divElements;
    }


    public List<DivElement> getDivElements(Map<String, String> param) {
        List<DivElement> divElements = new ArrayList<>();

        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (!entry.getKey().equals("cardId")) {
                divElements.add(new DivElement(entry.getKey(), Integer.parseInt(entry.getValue())));
            }
        }
        // Get the last 4 elements from the list
        List<DivElement> lastFourDivElements = divElements.size() >= 4
                ? divElements.subList(divElements.size() - 4, divElements.size())
                : divElements;

        // Optionally, sum the values of the last 4 elements and add a new DivElement with the sum
        int sum = 0;
        for (DivElement divElement : lastFourDivElements) {
            sum += divElement.getContent(); // Assuming DivElement has a method getValue() that returns its integer value
        }
        divElements.add(new DivElement("div-14", sum));

        return divElements;
    }

    /**
     * Updates the content of a specific DivElement based on the cardId.
     *
     * @param divElements the list of DivElement objects
     * @param cardId      the ID of the card triggering the update
     * @return true if a matching DivElement was updated, false otherwise
     */
    private boolean updateDivElementContent(List<DivElement> divElements, String cardId) {
        String btnIDModified = cardId.replace("btn", "div").replace("-n", "");

        for (DivElement divElement : divElements) {

            if (divElement.getId().equals(btnIDModified)) {
                int divContent = divElement.getContent();
                divElement.setContent(cardId.contains("-n") ? divContent - 1 : divContent + 1);
                return true; // Successfully updated
            }


        }
        return false; // No matching element found
    }


}
