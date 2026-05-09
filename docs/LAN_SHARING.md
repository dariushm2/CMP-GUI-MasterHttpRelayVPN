# LAN Sharing

By default, MasterHttpRelayVPN listens only on `127.0.0.1`, so only the same computer can use it. LAN sharing lets phones, tablets, or other computers on your local network use your proxy.

## When To Use It

Use LAN sharing only on a trusted private network. Anyone who can reach the proxy can send traffic through it.

## Enable LAN Sharing

Set this in `config.json`:

```json
{
  "lan_sharing": true,
  "listen_host": "0.0.0.0",
  "http_port": 8085
}
```

Restart the proxy. The startup log prints LAN addresses other devices can use.

## Configure Other Devices

On the other device, set the HTTP proxy to:

| Field | Value |
|-------|-------|
| Address | Your computer's LAN IP from the startup log |
| Port | `8085` |
| Type | HTTP |

If the other device browses HTTPS websites, it also needs to trust the CA certificate from `ca/ca.crt`.

## Safety Checklist

- Use this only on networks you trust.
- Turn it off when you do not need it.
- Keep `auth_key` private.
- Never share the `ca/` folder.
- Prefer `127.0.0.1` for normal single-computer use.
