## Fast Food Simulator

You can see detailed description of the project [here](https://github.com/plplmax/fast-food-simulator/blob/master/fast-food-simulator.pdf).

## Stack

- [Kotlin](https://kotlinlang.org/) (1.6.10)
- [Compose Multiplatform for UI](https://github.com/JetBrains/compose-jb) (1.1.1)

## Demo

![image](https://user-images.githubusercontent.com/50287455/193414238-5a1ee7d0-9ea7-492b-af30-3132bcfb51e9.png)

## Build

```
git clone git@github.com:plplmax/fast-food-simulator.git
```

```
cd fast-food-simulator
```

To build and run locally:

```
./gradlew run
```

To only build:

```
./gradlew package(Deb|Dmg|Msi|UberJarForCurrentOS)
```

After a build, output binaries can be found in `${project.buildDir}/compose/binaries`.

For specific problems you can see [that](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution) tutorial by [JetBrains](https://www.jetbrains.com/).
