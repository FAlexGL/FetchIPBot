# FetchIPBot

A Linux daemon that monitors your public IP address and sends a Telegram message whenever it changes.

---

## Requirements

- Java 17 or higher
- A Linux system with systemd
- A Telegram bot token and chat ID

---

## Files

Before starting, make sure you have the following files:

| File | Description |
|---|---|
| `fetchipbot.jar` | The application |
| `fetchipbot` | The bash wrapper |
| `fetchipbot.service` | The systemd service file |

---

## Installation

### 1. Create the install directory

```bash
sudo mkdir -p /opt/fetchipbot
sudo cp fetchipbot.jar /opt/fetchipbot/
sudo chown -R yourusername:yourusername /opt/fetchipbot
```

> Replace `yourusername` with the user that will run the service.

---

### 2. Initial Telegram setup

Run the app once interactively to create the `setting.txt` file with your bot token and chat ID:

```bash
java -jar /opt/fetchipbot/fetchipbot.jar
```

Follow the on-screen instructions. When the menu appears, select **option 3 (Exit)**.

> This step only needs to be done once. The credentials are stored in `/opt/fetchipbot/setting.txt`.

---

### 3. Install the systemd service

Copy the service file to the systemd directory:

```bash
sudo cp fetchipbot.service /etc/systemd/system/fetchipbot.service
```

Open it and edit the following field:

```bash
sudo nano /etc/systemd/system/fetchipbot.service
```

```ini
User=yourusername
```

Then reload systemd and enable the service:

```bash
sudo systemctl daemon-reload
sudo systemctl enable fetchipbot
```

---

### 4. Install the bash wrapper

```bash
sudo cp fetchipbot /usr/local/bin/fetchipbot
sudo chmod +x /usr/local/bin/fetchipbot
```

---

### 5. Start the bot

```bash
fetchipbot --daemon 5
```

---

### 6. Verify it is running

```bash
fetchipbot --status
fetchipbot --logs
```

---

## Usage

| Command | Description |
|---|---|
| `fetchipbot` | Launch interactive menu |
| `fetchipbot --start` | Start the daemon |
| `fetchipbot --stop` | Stop the daemon |
| `fetchipbot --restart` | Restart the daemon |
| `fetchipbot --status` | Show daemon status |
| `fetchipbot --set-period <minutes>` | Change the check period and restart |
| `fetchipbot --logs` | Show last 50 log lines |
| `fetchipbot --logs <N>` | Show last N log lines (0 = all) |
| `fetchipbot --logs-live` | Follow logs in real time |
| `fetchipbot --version` | Show version number |
| `fetchipbot --description` | Show app description |
| `fetchipbot --help` | Show help message |

---

## How it works

The daemon checks your public IP address every N minutes using [ipify.org](https://api.ipify.org). If the IP has changed since the last check, it sends the new IP to your Telegram chat.

Logs are stored in `/opt/fetchipbot/app_*.log`.

---

## Uninstall

```bash
fetchipbot --stop
sudo systemctl disable fetchipbot
sudo rm /etc/systemd/system/fetchipbot.service
sudo systemctl daemon-reload
sudo rm -rf /opt/fetchipbot
sudo rm /usr/local/bin/fetchipbot
```

