package org.aksw.geiser.limes.rest;

import org.aksw.geiser.limes.LimesRequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
public class LimesController {
	
	@Autowired
	protected LimesRequestProcessor processor;
	
	@RequestMapping("/complete/{id}")
	public ResponseEntity<Boolean> handleComplete(@PathVariable(name="id", required=true) String id) {
		boolean jobFinished = processor.isJobFinished(id);
		return new ResponseEntity<Boolean>(jobFinished, HttpStatus.OK);
	}

}
