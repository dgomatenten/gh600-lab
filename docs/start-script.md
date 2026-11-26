# Startup Script (`start.ps1` / `start.cmd`)

**Location:** [start.ps1](../start.ps1), [start.cmd](../start.cmd)

A root-level startup script that launches both the Spring Boot backend (`:8080`) and the React + Vite frontend (`:5173`) with one command.

## What it does

1. **Verifies prerequisites:** checks that `java` and `npm` are available on `PATH`.
2. **Checks ports:** warns and exits if port `8080` or `5173` is already in use.
3. **Installs frontend dependencies:** runs `npm install` automatically if `frontend/node_modules/` does not exist.
4. **Runs both servers:**
   - **Default (single terminal):** starts both servers as PowerShell background jobs, streams output prefixed with `[backend]` and `[frontend]`, and stops both process trees cleanly when you press `Ctrl+C`.
   - **Separate windows (`-SeparateWindows`):** opens two dedicated PowerShell windows (one for Spring Boot, one for Vite).

## Usage

From the repository root in PowerShell:

```powershell
# Start both servers in the current terminal (Ctrl+C stops both)
.\start.ps1

# Start both servers and open http://localhost:5173 in your browser when ready
.\start.ps1 -OpenBrowser

# Start backend and frontend in two separate PowerShell windows
.\start.ps1 -SeparateWindows

# Stop any processes listening on ports 8080 and 5173
.\start.ps1 -Stop
```

From Command Prompt (`cmd.exe`) or by double-clicking in File Explorer:

```cmd
start.cmd
start.cmd -OpenBrowser
start.cmd -Stop
```

