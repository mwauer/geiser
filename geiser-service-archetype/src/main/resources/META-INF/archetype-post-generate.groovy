println "Setting mvnw executable"

def file = new File( request.getOutputDirectory(), "mvnw" );
file.setExecutable(true, false);

println "Done"