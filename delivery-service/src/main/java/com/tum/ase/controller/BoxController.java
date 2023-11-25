package com.tum.ase.controller;

import com.tum.ase.constant.ErrorMsg;
import com.tum.ase.constant.UserRole;
import com.tum.ase.exception.notfound.BoxNotFoundException;
import com.tum.ase.exception.user.FailUpdateCredentialException;
import com.tum.ase.model.Box;
import com.tum.ase.service.BoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/box")
public class BoxController {
    private final BoxService boxService;

    @Autowired
    public BoxController(BoxService boxService) {
        this.boxService = boxService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<List<Box>> getAllBoxes() {
		return new ResponseEntity<>(boxService.getAllBoxes(), HttpStatus.OK);
    }

    @GetMapping("/list/{userId}")
    @PreAuthorize("hasAuthority('" + UserRole.DELIVERER + "')")
    public ResponseEntity<List<Box>> getAllBoxesAssignedToDeliverer(@PathVariable("userId") String userId) {
        return new ResponseEntity<>(boxService.getAllBoxesAssignedToDeliverer(userId), HttpStatus.OK);
    }

    @GetMapping("/find/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Box> getBoxById(@PathVariable("id") String id) {
        return new ResponseEntity<>(boxService.getBoxById(id), HttpStatus.OK);
    }

	@PostMapping("/add")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Box> createBox(HttpServletRequest request, @RequestBody Box newBox) {
        String jwt = this.extractJWT(request);
        return new ResponseEntity<>(boxService.saveBox(newBox, jwt), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public ResponseEntity<Box> updateBox(HttpServletRequest request, @PathVariable("id") String id, @RequestBody Box updatedBox) throws BoxNotFoundException {
        String jwt = this.extractJWT(request);
        return new ResponseEntity<>(boxService.updateBox(id, updatedBox, jwt), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('" + UserRole.DISPATCHER + "')")
    public void deleteBoxes(HttpServletRequest request, @RequestParam("id") List<String> ids) throws BoxNotFoundException {
        String jwt = this.extractJWT(request);
        boxService.deleteBoxes(ids, jwt);
    }

    private String extractJWT(HttpServletRequest request) {
        String jwt = null;
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null && cookie.getValue() != null) {
            jwt = cookie.getValue();
        }
        if (jwt == null) {
            throw new FailUpdateCredentialException(ErrorMsg.FAIL_TO_UPDATE_CREDENTIAL);
        }
        return jwt;
    }
}
