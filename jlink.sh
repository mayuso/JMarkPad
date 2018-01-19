DESTINATION=build/dist
DEPS=build/deps
APP_JAR=build/libs/app.jar

# prepare the lib folder with Gradle
./gradlew copyLibs

# compile all Java sources into build/dist
javac -d $DESTINATION -p $DEPS $(find src/main/java -name *.java)

# copy all resources into build/dist
cp -r src/main/resources/* $DESTINATION

# jar everything into APP_JAR
jar cf APP_JAR -C $DESTINATION .

# you could run the app now by uncommenting out this line
#java -p $DEPS:$APP_JAR -cp CLASSPATH -m JMarkPad/ui.UI

# must remove the executable folder first
rm -rf executable

# create native executable with jlink
echo "Calling jlink"
jlink -p $JAVA_HOME/jmods:$DEPS:$APP_JAR \
  --add-modules JMarkPad \
  --launcher start-app=JMarkPad \
  --strip-debug --compress=2 \
  --output executable
