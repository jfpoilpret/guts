
/*
 *	${mavenStamp}
 */
// provides naming, version and build time
function gutsDescriptor () {
	//
	this.mainJar	=	'${mainJar}';
	this.mainClass	=	'${mainClass}';
	//
	this.finalName	=	'${project.build.finalName}';
	this.groupId	=	'${project.groupId}';
	this.artifactId	=	'${project.artifactId}';
	this.version	=	'${project.version}';
	this.timeStamp	=	'${timestamp}' ;
	//
};

var gutsDemo01 = new gutsDescriptor();
gutsDemo01.jnlpApplet = "${project.artifactId}-applet.jnlp";
gutsDemo01.jnlpApplication = "${project.artifactId}-application.jnlp";
gutsDemo01.jnlpAppletTest = "${project.artifactId}-applet-test.jnlp";
gutsDemo01.jnlpApplicationTest = "${project.artifactId}-application-test.jnlp";


