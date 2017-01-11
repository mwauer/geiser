#! /bin/bash

# Generate the RDF/XML from the Turtle code

rapper -I "http://geiser-projekt.de/vocab/geiser#" -i turtle -o rdfxml-abbrev messagingontology.n3 > messagingontology.rdfs


