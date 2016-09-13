package de.kuhpfau.amqp.helloworld.receiver.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test REST controller, first step for a messaging proxy showcase
 * 
 * @author wauer
 *
 */
@Component
@RestController
public class HelloWorldController {

	@RequestMapping("/hello")
	public ResponseEntity<String> handle(
			@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
		model.addAttribute("name", name);
		return new ResponseEntity<String>("Hello " + name, HttpStatus.OK);
	}

}
