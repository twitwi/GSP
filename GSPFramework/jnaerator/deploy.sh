
echo TODO mvn deploy:deploy-file -DgroupId=com.ochafik \
  -DartifactId=jnaerator \
  -Dversion=0.7 \
  -Dpackaging=jar \
  -Dfile=jnaerator-v0.7-b408.jar \
  -DrepositoryId=releases \
  -Durl=http://oberon:8081/nexus/content/repositories/releases

