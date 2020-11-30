# Vistas
 A library and tool for creating and customizing main menu panoramas and music for mods and modpacks on the fabric platform

## As a library
build.gradle
```groovy
repositories {
 maven {
  url 'https://jitpack.io'
 }
}
```

```groovy
dependencies {
 modImplementation "com.github.LudoCrypt:Vistas:${project.vistas_version}"
 include "com.github.LudoCrypt:Vistas:${project.vistas_version}"
}
```

gradle.properties
```groovy
vistas_version=a.b.c
```
replace a.b.c with the latest version. For example, 1.0.0.

## Use in code

```java
Panorama.addPanorama(new Identifier("minecraft"), new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));
// Or
Panorama.addPanorama(new Identifier("minecraft"), new Identifier("textures/gui/title/background/panorama"), new MusicSound(new SoundEvent(new Identifier("music.menu")), 20, 600, true));
```
The first identifier being the name in which config uses, the second identifier being which panorama to choose, and the third being what music to play.
