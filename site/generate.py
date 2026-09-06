#!/usr/bin/env python3
"""Generate the Cerebro GitHub Pages site from strategy JSONs."""

import argparse
import html
import json
import shutil
import sys
from pathlib import Path

# Category metadata: constant name → (display title, slug, icon, blurb)
CATEGORIES = {
    "Perspective": (
        "Perspective",
        "perspective",
        "perspective.svg",
        "See situations from different angles.",
    ),
    "Experimentation": (
        "Experimentation",
        "experimentation",
        "experimentation.svg",
        "Try new approaches and learn from results.",
    ),
    "Clarity": (
        "Clarity",
        "clarity",
        "clarity.svg",
        "Cut through confusion and find focus.",
    ),
    "Mindset": (
        "Mindset",
        "mindset",
        "brain.svg",
        "Shift your mental frame and beliefs.",
    ),
    "DecisionMaking": (
        "Decision Making",
        "decision-making",
        "decision.svg",
        "Choose with confidence and clarity.",
    ),
    "Improvement": (
        "Improvement",
        "improvement",
        "brain.svg",
        "Learn and grow from experience.",
    ),
}


def load_strategies(asset_dir: Path) -> list[dict]:
    """Load all strategies from the strategy JSON directory."""
    strategies_dir = asset_dir / "strategies"
    if not strategies_dir.exists():
        raise FileNotFoundError(f"Strategies directory not found: {strategies_dir}")

    strategies = []
    for json_file in sorted(strategies_dir.glob("*.json")):
        data = json.loads(json_file.read_text(encoding="utf-8"))
        strategies.extend(data)
    return strategies


def validate_strategies(strategies: list[dict]) -> None:
    """Validate strategy data for consistency."""
    errors = []
    warnings = []
    seen_titles = {}

    for i, strat in enumerate(strategies):
        # Check required keys
        for key in ("title", "short_description", "long_description", "category"):
            if key not in strat:
                errors.append(f"Strategy {i} missing key '{key}'")

        # Check category
        category = strat.get("category")
        if category not in CATEGORIES:
            errors.append(
                f"Strategy {i} ('{strat.get('title')}') has unknown category '{category}'"
            )

        # Warn on duplicates (don't error—they exist in the original data)
        title = strat.get("title")
        if title in seen_titles:
            warnings.append(f"Duplicate strategy title: '{title}' (also in {seen_titles[title]})")
        seen_titles[title] = category

    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        sys.exit(1)

    if warnings:
        for warning in warnings:
            print(f"WARNING: {warning}", file=sys.stderr)


def copy_icons(static_dir: Path) -> None:
    """Copy SVG icons from app assets to site static."""
    icons_dir = static_dir / "icons"
    icons_dir.mkdir(exist_ok=True, parents=True)
    app_svg_dir = Path("app/svgAssets")

    if not app_svg_dir.exists():
        print(f"Warning: {app_svg_dir} not found, skipping icon copy", file=sys.stderr)
        return

    for icon in ("perspective.svg", "experimentation.svg", "clarity.svg", "decision.svg", "brain.svg", "logo.svg"):
        src = app_svg_dir / icon
        if src.exists():
            shutil.copy2(src, icons_dir / icon)


def generate_index(build_dir: Path, strategies: list[dict]) -> None:
    """Generate the landing page (index.html)."""
    # Count strategies per category
    counts = {}
    for strat in strategies:
        cat = strat.get("category")
        counts[cat] = counts.get(cat, 0) + 1

    category_cards = []
    for const_name in (
        "Perspective",
        "Experimentation",
        "Clarity",
        "Mindset",
        "DecisionMaking",
        "Improvement",
    ):
        title, slug, icon, blurb = CATEGORIES[const_name]
        count = counts.get(const_name, 0)
        category_cards.append(
            f"""
          <a href="library.html#{slug}" class="category-card">
            <img src="static/icons/{icon}" alt="{title}" class="category-icon" />
            <div class="category-info">
              <h3>{title}</h3>
              <p>{blurb}</p>
              <span class="count">{count} strategies</span>
            </div>
          </a>
        """
        )

    html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Cerebro — Think in new ways</title>
    <link rel="icon" href="static/icons/logo.svg" />
    <link rel="stylesheet" href="static/site.css" />
</head>
<body>
    <header class="hero">
        <img src="static/icons/logo.svg" alt="Cerebro" class="logo" />
        <h1>Cerebro</h1>
        <p class="tagline"><em>Think in new ways.</em></p>
        <p class="pitch">
            A library of thinking strategies to help you approach problems from new angles,
            question assumptions, and find creative solutions.
        </p>
    </header>

    <main>
        <section class="categories">
            <h2>Six ways of thinking</h2>
            <div class="category-grid">
                {''.join(category_cards)}
            </div>
        </section>

        <section class="about">
            <h2>About Cerebro</h2>
            <p>
                Cerebro is a small Android app that collects a curated library of thinking
                strategies—short prompts and techniques you can apply whenever you're stuck.
                Each strategy has a title, a one-line summary, and a full explanation to help
                you step outside your usual framing, question assumptions, and work through a
                situation from a different angle.
            </p>
            <p>
                <strong><a href="library.html">Browse all {len(strategies)} strategies →</a></strong>
            </p>
        </section>

        <section class="cta">
            <h2>Get the app</h2>
            <p>
                <a href="https://github.com/Tosaa/Cerebro" class="button">View on GitHub</a>
            </p>
        </section>
    </main>

    <footer>
        <p>
            Licensed under the <a href="https://www.apache.org/licenses/LICENSE-2.0">
            Apache License 2.0</a>.
        </p>
    </footer>
</body>
</html>
"""
    (build_dir / "index.html").write_text(html_content, encoding="utf-8")


def generate_library(build_dir: Path, strategies: list[dict]) -> None:
    """Generate the strategy library page (library.html)."""
    # Group strategies by category
    by_category = {}
    for strat in strategies:
        cat = strat.get("category")
        if cat not in by_category:
            by_category[cat] = []
        by_category[cat].append(strat)

    # Build category filter chips and slug-to-category mapping
    category_chips = []
    slug_to_category = {}
    for const_name in (
        "Perspective",
        "Experimentation",
        "Clarity",
        "Mindset",
        "DecisionMaking",
        "Improvement",
    ):
        title, slug, _, _ = CATEGORIES[const_name]
        category_chips.append(
            f'<button class="filter-chip" data-category="{const_name}">{title}</button>'
        )
        slug_to_category[slug] = const_name

    # Build strategy cards (pre-rendered HTML)
    strategy_cards = []
    for strat in strategies:
        category = strat.get("category")
        title = html.escape(strat.get("title", "Untitled"))
        short = html.escape(strat.get("short_description", ""))
        long = html.escape(strat.get("long_description", ""))

        # Escape line breaks in long description for display
        long_display = long.replace("\n", "<br />")

        strategy_cards.append(
            f"""
          <article class="strategy-card" data-category="{category}" data-title="{html.escape(strat.get('title', ''))}">
            <h3>{title}</h3>
            <p class="short-description">{short}</p>
            <details class="long-description">
              <summary>Read more</summary>
              <div>{long_display}</div>
            </details>
          </article>
        """
        )

    html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Strategy Library — Cerebro</title>
    <link rel="icon" href="static/icons/logo.svg" />
    <link rel="stylesheet" href="static/site.css" />
</head>
<body>
    <header class="page-header">
        <a href="index.html" class="back-link">← Back</a>
        <h1>Strategy Library</h1>
        <p>All {len(strategies)} strategies, searchable and filterable.</p>
    </header>

    <main>
        <div class="controls">
            <input
                type="text"
                id="search-box"
                class="search-box"
                placeholder="Search strategies by title or keyword…"
                aria-label="Search"
            />
            <div class="filters">
                <button class="filter-chip active" data-category="all">All</button>
                {''.join(category_chips)}
            </div>
        </div>

        <div class="strategies">
            {''.join(strategy_cards)}
        </div>

        <div id="no-results" class="no-results" style="display: none;">
            <p>No strategies match your search. Try different keywords.</p>
        </div>
    </main>

    <footer>
        <p>
            Licensed under the <a href="https://www.apache.org/licenses/LICENSE-2.0">
            Apache License 2.0</a>.
        </p>
    </footer>

    <script>
        const searchBox = document.getElementById("search-box");
        const filterChips = document.querySelectorAll(".filter-chip");
        const cards = document.querySelectorAll(".strategy-card");
        const noResults = document.getElementById("no-results");

        // Map from slug (from URL) to category constant name (from data)
        const slugToCategoryMap = {json.dumps(slug_to_category)};

        let activeCategory = "all";
        let searchQuery = "";

        function updateDisplay() {{
            let visibleCount = 0;

            cards.forEach((card) => {{
                const categoryMatch =
                    activeCategory === "all" || card.dataset.category === activeCategory;
                const searchMatch =
                    !searchQuery ||
                    card.dataset.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                    card.textContent.toLowerCase().includes(searchQuery.toLowerCase());

                const visible = categoryMatch && searchMatch;
                card.style.display = visible ? "" : "none";
                if (visible) visibleCount++;
            }});

            noResults.style.display = visibleCount === 0 ? "block" : "none";
            updateHash();
        }}

        function updateHash() {{
            if (activeCategory !== "all") {{
                // Use the slug (from the category's URL) not the constant name
                const slugEntry = Object.entries(slugToCategoryMap).find(
                    ([_, cat]) => cat === activeCategory
                );
                window.location.hash = slugEntry ? slugEntry[0] : activeCategory;
            }} else {{
                window.location.hash = "";
            }}
        }}

        searchBox.addEventListener("input", (e) => {{
            searchQuery = e.target.value;
            updateDisplay();
        }});

        filterChips.forEach((chip) => {{
            chip.addEventListener("click", () => {{
                filterChips.forEach((c) => c.classList.remove("active"));
                chip.classList.add("active");
                activeCategory = chip.dataset.category || "all";
                updateDisplay();
            }});
        }});

        // Restore category from hash on page load
        window.addEventListener("hashchange", () => {{
            const hash = window.location.hash.slice(1);
            if (hash) {{
                // Translate slug to category name
                const categoryName = slugToCategoryMap[hash] || hash;
                const chip = Array.from(filterChips).find((c) => c.dataset.category === categoryName);
                if (chip) {{
                    filterChips.forEach((c) => c.classList.remove("active"));
                    chip.classList.add("active");
                    activeCategory = categoryName;
                    updateDisplay();
                }}
            }}
        }});

        // Initial hash restore
        const initialHash = window.location.hash.slice(1);
        if (initialHash) {{
            // Translate slug to category name
            const categoryName = slugToCategoryMap[initialHash] || initialHash;
            const chip = Array.from(filterChips).find((c) => c.dataset.category === categoryName);
            if (chip) {{
                filterChips.forEach((c) => c.classList.remove("active"));
                chip.classList.add("active");
                activeCategory = categoryName;
            }}
        }}
    </script>
</body>
</html>
"""
    (build_dir / "library.html").write_text(html_content, encoding="utf-8")


def generate_data_json(build_dir: Path, strategies: list[dict]) -> None:
    """Write strategies as a JSON file."""
    data_dir = build_dir / "data"
    data_dir.mkdir(exist_ok=True, parents=True)
    (data_dir / "strategies.json").write_text(
        json.dumps(strategies, indent=2, ensure_ascii=False) + "\n",
        encoding="utf-8",
    )


def main():
    parser = argparse.ArgumentParser(
        description="Generate Cerebro GitHub Pages site from strategy JSONs"
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="Validate strategy data and exit without generating",
    )
    parser.add_argument(
        "--asset-dir",
        type=Path,
        default=Path("app/src/main/assets"),
        help="Path to app assets directory (default: app/src/main/assets)",
    )
    parser.add_argument(
        "--build-dir",
        type=Path,
        default=Path("site/_build"),
        help="Path to output directory (default: site/_build)",
    )
    args = parser.parse_args()

    # Load strategies
    try:
        strategies = load_strategies(args.asset_dir)
    except FileNotFoundError as e:
        print(f"ERROR: {e}", file=sys.stderr)
        sys.exit(1)

    # Validate
    validate_strategies(strategies)
    print(f"✓ Validated {len(strategies)} strategies")

    if args.check:
        print("Validation passed. --check mode, exiting.")
        return

    # Generate site
    args.build_dir.mkdir(exist_ok=True, parents=True)
    (args.build_dir / "static").mkdir(exist_ok=True, parents=True)

    copy_icons(args.build_dir / "static")
    print("✓ Copied icons")

    generate_index(args.build_dir, strategies)
    print("✓ Generated index.html")

    generate_library(args.build_dir, strategies)
    print("✓ Generated library.html")

    generate_data_json(args.build_dir, strategies)
    print("✓ Generated data/strategies.json")

    # Copy CSS
    css_path = Path("site/static/site.css")
    if css_path.exists():
        shutil.copy2(css_path, args.build_dir / "static" / "site.css")
        print("✓ Copied CSS")

    # Create .nojekyll
    (args.build_dir / ".nojekyll").touch()
    print("✓ Created .nojekyll")

    print(f"\n✅ Site generated to {args.build_dir}")


if __name__ == "__main__":
    main()
