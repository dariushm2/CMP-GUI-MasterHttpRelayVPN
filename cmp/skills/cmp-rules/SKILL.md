---
name: cmp-rules
description: Development guidelines, coding standards, and architectural rules for the CMP module in this fork project.
---
# CMP Development Rules and Guidelines

This skill enforces critical coding standards, architectural patterns, and structural rules for all work on the CMP (Kotlin Multiplatform/Compose Multiplatform) module in this repository.

## Rule 1: Fork Project Boundaries (Code Location)
- **Rule**: All custom/new code must be written within the `/cmp` directory.
- **Upstream Separation**: Avoid touching or modifying the code from upstream (parent repository files/folders outside `/cmp`) unless absolutely necessary and approved by the user. Keep the upstream files clean.

## Rule 2: Compose Architecture & Small Composables
- **Rule**: Never create large composables.
- **Decomposition**: Keep composables small, focused, and single-purpose.
- **Organization**: Always break down large composable components into smaller functions. Place them either:
  1. Within the same file if they are small and cohesive, OR
  2. Create a sub-package and distribute the sub-composables across multiple separate, descriptive files.
- **Modifier Parameter**: Always add `modifier: Modifier = Modifier` (or equivalent) as the **last parameter** to every Composable function. This enables standard modifier chaining and layout customizability from the parent caller.
- **Line Limit**: Never exceed 60 lines of code inside a single Composable function. If a Composable function grows beyond 60 lines, immediately decompose it into smaller helper Composables or separate files.

## Rule 3: ViewModel Context Guidelines
- **Rule**: Never inject `Context` (Android Context) into any `ViewModel`.
- **Reason**: Injecting Context into ViewModels causes memory leaks (as ViewModels can outlive the Activity/Context lifecycle) and violates clean architecture by coupling UI/Platform layers with logic layers.
- **Alternative**: Use repository abstractions, clean providers, or resource managers/delegates that do not hold hard references to a UI `Context`.

## Rule 4: Code Decoupling & Encapsulation
- **Rule**: Decouple the code aggressively.
- **Encapsulate Features**: Group files by feature/domain. Encapsulate implementation details and expose clean public interfaces.
- **Design Patterns**: Create and use:
  - **Delegates**: For composition-based behaviors and sharing logic.
  - **Managers**: For state or system operations.
  - **Providers**: For supplying resources or services.
- Keep components loosely coupled and highly cohesive.

## Rule 5: Resource Management (No Hardcoding)
- **Rule**: Never hard code strings, colors, dimensions, or other resources in Composable files or logic code.
- **Standard**: Always define and reference them through the proper resource system (e.g., KMP resources, XML files, theme colors, etc.).

## Rule 6: Import Hygiene & Optimization
- **Rule 6a**: Never use wildcard (`*`) imports. Always import classes/functions individually.
- **Rule 6b**: **Always optimize imports** after making any change in any file. Remove unused imports and sort them properly.

## Rule 7: File Length Limits
- **Rule**: Keep files small and readable.
- **Limit**: Files generally **must not exceed 500 lines**. If a file grows beyond 500 lines, refactor, decompose, and split it into multiple files or sub-packages.

## Rule 8: GitHub Pull Request Standards
- **Rule 8a (Title)**: Every pull request title must start with the related issue number enclosed in square brackets, followed by a space and the title.
  - *Example*: `[45] docs: add CMP development rules and guidelines skill`
- **Rule 8b (Description)**: The very first line of any pull request description must be `# #<issue_number>` (where `<issue_number>` is the numerical issue number).
  - *Example*: `# #45`
  - The rest of the PR description, breakdown, and references must go below this header line.

