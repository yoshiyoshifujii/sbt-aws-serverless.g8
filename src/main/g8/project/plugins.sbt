lazy val root = project.in(file(".")).dependsOn(githubRepo)

lazy val githubRepo = uri("git://github.com/yoshiyoshifujii/sbt-aws-serverless.git#master")

