# SpringPropsChecker

Maven plugin for Spring Boot projects that scans all profile-specific `application*.properties` files and ensures that all profiles contain consistent property keys.
Missing keys are highlighted in the console, helping prevent runtime misconfigurations.

# Usage
```
		<plugin>
			<groupId>com.ny</groupId>
			<artifactId>property-checker-maven-plugin</artifactId>
			<version>1.0.0</version>
			<executions>
				<execution>
					<id>check-properties</id>
					<phase>validate</phase>
					<goals>
						<goal>check</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
```
