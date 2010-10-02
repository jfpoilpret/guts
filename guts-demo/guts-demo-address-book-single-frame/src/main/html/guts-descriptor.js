
/*
 *	${mavenStamp}
 */
// provides naming, version and build time
function gutsDescriptor () {
	//
	this.mainJar	=	'${mainJar}';
	this.appClass	=	'${appClass}';
	this.appletClass=	'${appletClass}';
	//
	this.finalName	=	'${project.build.finalName}';
	this.groupId	=	'${project.groupId}';
	this.artifactId	=	'${project.artifactId}';
	this.version	=	'${project.version}';
	this.timeStamp	=	'${timestamp}' ;
	//
};

var gutsDemo01 = new gutsDescriptor();
gutsDemo01.jnlpApplet = "${jnlpApplet}";
gutsDemo01.jnlpAppletTest = "${jnlpAppletTest}";
gutsDemo01.jnlpApplication = "${jnlpApplication}";
gutsDemo01.jnlpApplicationTest = "${jnlpApplicationTest}";


