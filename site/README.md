# Site generation

The Cerebro website is generated from the strategy JSONs in `app/src/main/assets/strategies/`.

## Regenerate locally

```bash
python3 site/generate.py --check    # validate strategy data
python3 site/generate.py            # generate site into site/_build/
```

### Preview

```bash
python3 -m http.server 8000 -d site/_build
# Open http://localhost:8000/
```

## Structure

- **`generate.py`** — Python 3.9+, no external dependencies. Reads strategy JSONs, validates them, and writes HTML + CSS to `site/_build/`.
- **`static/site.css`** — Responsive stylesheet using the app's theme colors (warm amber/brown palette from `ui/theme2/Color.kt`).
- **`static/icons/`** — Strategy category icons, copied from `app/svgAssets/` at generation time.
- **`_build/`** — Generated output (ignored by git); published to GitHub Pages.

## How it works

`generate.py` produces:

- **`index.html`** — Landing page with project intro and category cards.
- **`library.html`** — Full strategy browser with search and category filtering (driven by `location.hash` for deep-linking).
- **`data/strategies.json`** — Combined JSON array, published for reference.
- **`.nojekyll`** — Tells GitHub Pages to serve verbatim (no Jekyll processing).

All internal links are **relative**, so the site works under the `/Cerebro/` sub-path on GitHub Pages.

## Category mapping

The `CATEGORIES` dict in `generate.py` mirrors `Category.kt`:

| Constant | Display | Icon | Count |
| --- | --- | --- | --- |
| `Perspective` | Perspective | perspective.svg | 11 |
| `Experimentation` | Experimentation | experimentation.svg | 11 |
| `Clarity` | Clarity | clarity.svg | 11 |
| `Mindset` | Mindset | brain.svg | 11 |
| `DecisionMaking` | Decision Making | decision.svg | 11 |
| `Improvement` | Improvement | brain.svg | 5 |

When you add a new category:
1. Add a `@Serializable enum` constant in `app/src/main/java/redtoss/creativity/cerebro/data/Category.kt`.
2. Add a corresponding JSON file in `app/src/main/assets/strategies/`.
3. Add an entry to the `CATEGORIES` dict in `generate.py`.
4. Commit and push — CI will validate and deploy.
