# JersuxsHome - Advanced Home Management System

## Overview
JersuxsHome is a powerful and user-friendly home management plugin for Minecraft servers. It allows players to set multiple homes, teleport between them, and even invite friends to visit their homes with an interactive invitation system.

## Features

### 🏠 Multi-Home System
- Set up to 5 custom homes (configurable limit)
- Easily teleport to any of your homes
- Rename and manage your homes with simple commands

### 🌍 World Permissions
- Configure which worlds allow home creation
- Separate permissions for overworld, nether, and end

### 🔄 Multi-Language Support
- Built-in support for English and Spanish
- Easily switch between languages in the configuration
- All messages fully customizable

### 💌 Interactive Home Invitations
- Invite friends to visit your homes
- Secure permission system for home visits
- Intuitive command system for sending and accepting invitations

### ✨ Teleportation Effects
- Configurable particle effects during teleportation
- Sound effects when teleporting (can be disabled)
- Customizable teleportation delay for balance

**Example:**

![ezgif-42c85cd69d9168-1](https://github.com/user-attachments/assets/ec14c307-2ba9-45a6-b4eb-d320ef7fdfe3)
## Commands

| Command | Description | Usage |
|---------|-------------|-------|
| `/home [name]` | Teleport to your home | `/home mansion` |
| `/sethome <name>` | Set a new home | `/sethome mansion` |
| `/delhome <name>` | Delete a home | `/delhome mansion` |
| `/edithome <name> <new_name>` | Rename a home | `/edithome mansion castle` |
| `/homes` | List all your homes | `/homes` |
| `/homeinvite <player> <home>` | Invite a player to your home | `/homeinvite Steve mansion` |
| `/homevisit <player> <home>` | Visit a player's home (requires invitation) | `/homevisit Steve mansion` |
| `/homereload` | Reload configuration and language files | `/homereload` |

## Configuration

The plugin is highly configurable. You can adjust:

- Maximum number of homes per player
- Teleportation delay time
- Teleportation effects (particles and sounds)
- World permissions for setting homes
- All plugin messages

## Permissions

- `homeplugin.reload` - Permission to reload the plugin configuration

## Installation

1. Download the plugin
2. Place it in your server's plugins folder
3. Restart your server
4. Configure the plugin in the config.yml file
5. Use `/homereload` to apply changes without restarting

Enjoy managing your homes with style and convenience!
