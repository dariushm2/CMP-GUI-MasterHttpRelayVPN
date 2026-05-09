# MasterHttpRelayVPN

[![GitHub](https://img.shields.io/badge/GitHub-MasterHttpRelayVPN-blue?logo=github)](https://github.com/masterking32/MasterHttpRelayVPN) [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/masterking32/MasterHttpRelayVPN) [![oosmetrics](https://api.oosmetrics.com/api/v1/badge/achievement/85a1f608-5c6d-4fcd-9b7f-b1ff8b680852.svg)](https://oosmetrics.com/repo/masterking32/MasterHttpRelayVPN) [![oosmetrics](https://api.oosmetrics.com/api/v1/badge/achievement/de9bee73-bc68-4f98-ba83-6957007046b1.svg)](https://oosmetrics.com/repo/masterking32/MasterHttpRelayVPN)

**Language:** English | [Persian / فارسی](README_FA.md)

MasterHttpRelayVPN is a local proxy that routes browser traffic through a Google Apps Script relay using domain fronting. The simple path needs only this project and a free Google account. For sites that block Google egress, you can optionally add an exit node later.

```text
Browser -> Local proxy -> Google front -> Your Apps Script relay -> Target site
                         network filter sees a Google-facing connection
```

## Choose Your Path

| I want to... | Go here |
|-------------|---------|
| Set it up for the first time | [Getting Started](docs/GETTING_STARTED.md) |
| Run with Docker | [Docker Guide](docs/DOCKER.md) |
| Share the proxy on my LAN | [LAN Sharing](docs/LAN_SHARING.md) |
| Use an exit node for blocked destinations | [Exit Node Guide](docs/exit-node/EXIT_NODE_DEPLOYMENT.md) |
| Understand every config option | [Configuration Reference](docs/CONFIGURATION.md) |
| Fix a problem | [Troubleshooting](docs/TROUBLESHOOTING.md) |
| Review safety notes | [Security Notes](docs/SECURITY.md) |
| Understand the internals | [Architecture](docs/ARCHITECTURE.md) |

## Fast Start

Before running the local proxy, deploy the Google relay from [apps_script/Code.gs](apps_script/Code.gs) and keep two values ready:

- `Deployment ID` from Google Apps Script
- `AUTH_KEY`, a long secret that must match `auth_key` in your local config

Download the project with either Git or ZIP, then run the one-click launcher.

**Option A: Git**

```bash
git clone https://github.com/masterking32/MasterHttpRelayVPN.git
cd MasterHttpRelayVPN
```

**Option B: ZIP**

1. Open [the GitHub repository](https://github.com/masterking32/MasterHttpRelayVPN).
2. Click **Code** -> **Download ZIP**.
3. Extract the ZIP file.
4. Open a terminal inside the extracted `MasterHttpRelayVPN` folder.

Then start the app:

**Windows**

```cmd
start.bat
```

**Linux / macOS**

```bash
chmod +x start.sh
./start.sh
```

The launcher creates a virtual environment, installs dependencies, opens the setup wizard if `config.json` is missing, and starts the proxy.

After it starts, configure your browser to use:

| Field | Value |
|-------|-------|
| Proxy type | HTTP |
| Address | `127.0.0.1` |
| Port | `8085` |
| SOCKS5 port, optional | `1080` |

For HTTPS sites, install the generated certificate from `ca/ca.crt` if the app cannot install it automatically. The full setup is in [Getting Started](docs/GETTING_STARTED.md).

## Common Next Steps

- If the browser shows certificate warnings, open [Troubleshooting](docs/TROUBLESHOOTING.md#certificate-errors).
- If you see `unauthorized`, make sure `AUTH_KEY` in [apps_script/Code.gs](apps_script/Code.gs) exactly matches `auth_key` in `config.json`.
- If browsing is slow or connections time out, run `python main.py --scan` and see [Configuration Reference](docs/CONFIGURATION.md#diagnostic-commands).
- If ChatGPT, Turnstile, or similar sites block the Google exit IP, use [Exit Node Guide](docs/exit-node/EXIT_NODE_DEPLOYMENT.md).

## Support And Updates

- Telegram channel: [https://t.me/masterdnsvpn](https://t.me/masterdnsvpn)
- Windows client: [MHRWindowsApp](https://github.com/AriPath/MHRWindowsApp)
- Ad blocker filter source: [PersianBlocker](https://github.com/MasterKia/PersianBlocker/)

## Safety

This project is provided for educational, testing, and research use. You are responsible for following applicable laws and service terms. Never share `config.json`, `auth_key`, `ca/`, or an exit-node URL together with a valid PSK. Read [Security Notes](docs/SECURITY.md) before sharing the proxy with other devices.

## License

MIT
