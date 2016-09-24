import com.github.yoshiyoshifujii.aws.apigateway._

lazy val regionName = sys.props.getOrElse("AWS_REGION", "")
lazy val accountId = sys.props.getOrElse("AWS_ACCOUNT_ID", "")
lazy val restApiId = sys.props.getOrElse("AWS_REST_API_ID", "")
lazy val roleArn = sys.props.getOrElse("AWS_ROLE_ARN", "")
lazy val bucketName = sys.props.getOrElse("AWS_BUCKET_NAME", "")
lazy val authKey = sys.props.getOrElse("AUTH_KEY", "")

val commonSettings = Seq(
  version := "$version$",
  scalaVersion := "2.11.8",
  organization := "$organization$"
)

val assemblySettings = Seq(
  assemblyMergeStrategy in assembly := {
    case "application.conf" => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  },
  assemblyJarName in assembly := s"\${name.value}-\${version.value}.jar",
  publishArtifact in (Compile, packageBin) := false,
  publishArtifact in (Compile, packageSrc) := false,
  publishArtifact in (Compile, packageDoc) := false
)

val awsSettings = Seq(
  awsRegion := regionName,
  awsAccountId := accountId
)

val apiGatewaySettings = awsSettings ++ Seq(
  awsApiGatewayRestApiId := restApiId,
  awsApiGatewayYAMLFile := file("./swagger.yaml"),
  awsApiGatewayResourceUriLambdaAlias := "\${stageVariables.env}",
  awsApiGatewayStages := Seq(
    "dev" -> Some("development stage"),
    "test" -> Some("test stage"),
    "v1" -> Some("v1 stage")
  ),
  awsApiGatewayStageVariables := Map(
    "dev" -> Map("env" -> "dev", "region" -> regionName),
    "test" -> Map("env" -> "test", "region" -> regionName),
    "v1" -> Map("env" -> "production", "region" -> regionName)
  )
)

val lambdaSettings = apiGatewaySettings ++ Seq(
  awsLambdaFunctionName := s"\${name.value}",
  awsLambdaDescription := "sample-serverless",
  awsLambdaRole := roleArn,
  awsLambdaTimeout := 15,
  awsLambdaMemorySize := 1536,
  awsLambdaS3Bucket := bucketName,
  awsLambdaDeployDescription := s"\${version.value}",
  awsLambdaAliasNames := Seq(
    "test", "production"
  )
)

lazy val root = (project in file(".")).
  enablePlugins(AWSApiGatewayPlugin).
  aggregate(hello).
  settings(commonSettings: _*).
  settings(apiGatewaySettings: _*).
  settings(
    name := "$name$"
  )

lazy val hello = (project in file("./modules/hello")).
  enablePlugins(AWSServerlessPlugin).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(lambdaSettings: _*).
  settings(
    name := "SampleHello",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      "io.spray" %%  "spray-json" % "1.3.2"
    ),
    awsLambdaHandler := "com.example.Hello::handleRequest",
    awsApiGatewayResourcePath := "/hellos",
    awsApiGatewayResourceHttpMethod := "GET",
    awsApiGatewayIntegrationRequestTemplates := Seq(
      "application/json" ->
        """{"stage":{"env":"\$stageVariables.env","region":"\$stageVariables.region"},"body":\$input.json('\$')}"""
    ),
    awsApiGatewayIntegrationResponseTemplates := ResponseTemplates(
      ResponseTemplate("200", None)
    ),
    awsMethodAuthorizerName := "SampleAuth",
    awsTestHeaders := Seq("Authorization" -> authKey),
    awsTestSuccessStatusCode := 200
  )

lazy val auth = (project in file("./modules/auth")).
  enablePlugins(AWSCustomAuthorizerPlugin).
  settings(commonSettings: _*).
  settings(assemblySettings: _*).
  settings(lambdaSettings: _*).
  settings(
    name := "SampleAuth",
    libraryDependencies ++= Seq(
      "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      "io.spray" %%  "spray-json" % "1.3.2"
    ),
    awsLambdaHandler := "com.example.Auth::handleRequest",
    awsAuthorizerName := "SampleAuth",
    awsIdentitySourceHeaderName := "Authorization",
    awsAuthorizerResultTtlInSeconds := 3000
  )

