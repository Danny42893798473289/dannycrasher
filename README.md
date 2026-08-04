<p align="center">
  <strong>Danny Crasher</strong><br>
  Open-source Minecraft Fabric client for authorized server security testing<br>
  Crash &amp; exploit modules · Custom protocol layer · Fabric 1.21.4
</p>

<p align="center">
  <a href="https://github.com/Danny42893798473289/dannycrasher/releases/latest"><img src="https://img.shields.io/github/v/release/Danny42893798473289/dannycrasher?label=Release&color=4aa3ff&logo=github" alt="Release"></a>
  <a href="https://github.com/Danny42893798473289/dannycrasher/actions/workflows/ci.yml"><img src="https://github.com/Danny42893798473289/dannycrasher/actions/workflows/ci.yml/badge.svg" alt="CI"></a>
  <img src="https://img.shields.io/badge/Minecraft-1.21.4-6ecb5a?logo=minecraft&logoColor=white" alt="Minecraft 1.21.4">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/License-MIT-57ffad" alt="MIT">
</p>

---

## What is Danny Crasher?

**Danny Crasher** is a free, open-source Fabric client for **security researchers, server owners, and administrators** who need to **stress-test and audit Minecraft server defenses** in controlled, authorized environments.

It ships with a **module framework**, **custom packet pipeline**, and **recon utilities** so you can probe how your stack (Paper, Spigot, proxies, ViaVersion, anti-cheat) behaves under exploit and crash payloads.

> **Use only on servers you own or have explicit written permission to test.**

---

## Modules

| Module | Type | Target / notes |
|--------|------|----------------|
| **Book** | Crasher | Oversized writable book payloads (Paper & ViaVersion/ViaBackwards) |
| **Charged Projectiles** | Crasher | Projectile data-component bomb |
| **Written Book** | Crasher | Nested NBT pages in `written_book_content` |
| **Hash Stack** | Crasher | Hashed container-click slot maps (protocol 770+) |
| **Custom Data** | Crasher | Deep `custom_data` NBT trees |
| **Bundle Contents** | Crasher | Nested `bundle_contents` item lists |
| **Lore** | Crasher | Oversized `lore` / `item_name` text components |
| **Creative Book** | Paper | Creative-slot writable book bomb (**requires creative**) |
| **Creative Custom Data** | Paper | Creative-slot `custom_data` NBT bomb (**requires creative**) |
| **Creative Bundle** | Paper | Creative-slot nested `bundle_contents` (**requires creative**) |
| **Creative Lore** | Paper | Creative-slot lore / item_name bomb (**requires creative**) |
| **Creative Slot Spam** | Paper | Rapid creative-slot inventory sync pressure (**requires creative**) |
| **Bundle** | Exploit | 1.21.2+ bundle instant-crash (protocol 768/769/770) |

Open **ClickGUI** with `Right Shift` (includes a **Paper** tab for creative-mode modules). Chat command prefix defaults to `!`.

Paper creative modules send `SetCreativeModeSlot` packets — you must be in **creative mode on the server** for them to apply.

---

## Installation

| Requirement | Version |
|-------------|---------|
| Minecraft | **1.21.4** |
| Fabric Loader | **≥ 0.19.3** |
| Fabric API | [1.21.4 build](https://modrinth.com/mod/fabric-api) |
| ViaFabricPlus | [required](https://modrinth.com/mod/viafabricplus) |
| Java | **21** |

```
.minecraft/mods/
├── fabric-api-….jar
├── ViaFabricPlus-….jar
└── DannyCrasher-1.1.0.jar
```

Config: `.minecraft/config/dannycrasher/`

---

## Build

```bash
git clone https://github.com/Danny42893798473289/dannycrasher.git
cd dannycrasher
./gradlew build
# → build/libs/DannyCrasher-<version>.jar
```

### Run client (dev)

```bash
./gradlew runClient
```

Place ViaFabricPlus in `run/mods/` for local launches.

---

## Project layout

```
src/client/java/me/dannycrasher/client/
├── module/       # Crash & exploit modules + framework
├── protocol/     # Custom Netty packet pipeline
├── command/      # Chat command system
├── gui/          # ClickGUI & menus
├── alt/          # Multi-account testing
├── notify/       # Execution feedback toasts
└── hud/          # Server fingerprint overlay
```

---

## Responsible use

> **Danny Crasher is a security research tool, not a weapon.**

- Test **only** servers you **own** or where you have **explicit written authorization**.
- Unauthorized denial-of-service is **illegal** in most jurisdictions and violates Minecraft’s Terms of Service.
- The software is provided **as-is**. **You** are responsible for how it is used.

---

## Credits

**Danny Crasher** | **danny**

Built with [Fabric](https://fabricmc.net/) · [Fabric Loom](https://github.com/FabricMC/fabric-loom) · [MinecraftAuth](https://github.com/RaphiMC/MinecraftAuth)

Based on concepts from the open-source LiteClient / FreeClient project.

---

## License

[MIT License](LICENSE)
