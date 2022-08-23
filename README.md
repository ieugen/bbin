# bbin

**ALPHA STATUS**

Install any Babashka script or project with one command.

```
$ bbin install io.github.rads/watch
{:coords {:git/sha "d5f36aa54e685f42f9592a7f3dd28badc3588c08",
          :git/tag "v0.0.4",
          :git/url "https://github.com/rads/watch"},
 :lib io.github.rads/watch}
        
$ watch --version
watch 0.0.4

$ bbin install https://gist.githubusercontent.com/rads/da8ecbce63fe305f3520637810ff9506/raw/25e47ce2fb5f9a7f9d12a20423e801b64c20e787/portal.clj
{:coords {:http/url "https://gist.githubusercontent.com/rads/da8ecbce63fe305f3520637810ff9506/raw/25e47ce2fb5f9a7f9d12a20423e801b64c20e787/portal.clj"}}

# Open a Portal window with all installed scripts
$ portal <(bbin ls)
```

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Docs](#docs)
- [CLI](#cli)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Manual

**1. Install `bbin` CLI:**
```zsh
mkdir -p ~/.bbin/bin && curl -L -o ~/.bbin/bin/bbin https://raw.githubusercontent.com/rads/bbin/main/bbin && chmod +x ~/.bbin/bin/bbin
```

**2. Add `~/.bbin/bin` to `PATH`:**
```zsh
echo 'export PATH="$HOME/.bbin/bin:$PATH"' >> ~/.zshrc && exec /bin/zsh
```

## Usage

```
# Install a script from a qualified lib name
$ bbin install io.github.rads/watch
$ bbin install org.babashka/neil --git/url https://github.com/rads/neil.git --git/tag v0.1.43+bbin
$ bbin install org.babashka/http-server --mvn/version 0.1.11

# Install a script from a URL
$ bbin install https://gist.githubusercontent.com/rads/da8ecbce63fe305f3520637810ff9506/raw/25e47ce2fb5f9a7f9d12a20423e801b64c20e787/portal.clj

# Install a script from a local root
$ git clone https://github.com/rads/bbin.git ~/src/bbin
$ bbin install io.github.rads/bbin --local/root ~/src/bbin --as bbin-dev

# Remove a script
$ bbin uninstall watch

# Show installed scripts
$ bbin ls

# Show the bin path
$ bbin bin

# TODO: Not implemented yet, but possibly supported in the future
$ bbin install https://gist.github.com/1d7670142f8117fa78d7db40a9d6ee80.git
$ bbin install git@gist.github.com:1d7670142f8117fa78d7db40a9d6ee80.git
$ bbin install https://github.com/babashka/http-server/releases/download/v0.1.11/http-server.jar
$ bbin install foo.clj
$ bbin install ~/src/watch
$ bbin install http-server.jar
```

## Docs

- [CLI Docs](#cli)
- [Design Docs](docs/design.md)
- [Auto-Completion](docs/auto-completion.md)

## CLI

- [`bbin install [script]`](#bbin-install-script)
- [`bbin uninstall [script]`](#bbin-uninstall-script)
- [`bbin ls`](#bbin-ls)
- [`bbin bin`](#bbin-bin)

---

### `bbin install [script]`

**Install a script**

- The scripts will be installed to `~/.bbin/bin`.
- Each bin script is a self-contained shell script that fetches deps and invokes `bb` with the correct arguments.
- The bin scripts can be configured using the CLI options or the `:bbin/bin` key in `bb.edn`

**Example `bb.edn` Config:**

```clojure
{:bbin/bin {neil {:main-opts ["-f" "neil"]}}}
```

**Supported Options:**

- `--as`
- `--git/sha`
- `--git/tag`
- `--git/url`
- `--latest-sha`
- `--local/root`
- `--main-opts`
- `--mvn/version`

---

### `bbin uninstall [script]`

**Remove a script**

---

### `bbin ls`

**List installed scripts**

---

### `bbin bin`

**Display bbin bin folder**

- The default folder is `~/.bbin/bin`

---

## Contributing

If you'd like to contribute to `bbin`, you're welcome to create [issues for ideas, feature requests, and bug reports](https://github.com/rads/bbin/issues).

## License

`bbin` is released under the [MIT License](LICENSE).
