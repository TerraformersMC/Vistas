# Vistas
 A library and tool for creating and customizing main menu panoramas and music for mods and modpacks on the fabric platform

## Use in code

```java
Vistas.Panorama.addPanorama("minecraft:nether", new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));
// Or
Vistas.Panorama.addPanorama("minecraft:nether", new Identifier("textures/gui/title/background/panorama"), new MusicSound(new SoundEvent(new Identifier("music.menu")), 20, 600, true));
```
The first string being the name in which config uses, the second identifier being which panorama to choose, and the third being what music to play.

## Or with weight

```java
Panorama.addPanoramaWithWeight("minecraft:nether", new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")), 3);
```
Makes
```java
Panorama.addPanorama("minecraft:nether_0", new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));
Panorama.addPanorama("minecraft:nether_1", new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));
Panorama.addPanorama("minecraft:nether_2", new Identifier("textures/gui/title/background/panorama"), new SoundEvent(new Identifier("music.menu")));
```
