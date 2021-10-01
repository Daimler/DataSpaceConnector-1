**Please note**

### Work in progress

All content reflects the current state of discussion, not final decisions.

---

# Architecture

## Configuration

Each EDC extension may have its own configuration settings. Depending on the used Configuration Extension the setting of
these keys may vary.

For information about the different EDC extensions please have a look at their corresponding README.md files.

For a more detailed explanation of the configuration itself please see [configuration.md](configuration.md).

## Data Transfer

### Contract

Before each data transfer a contract must be offered from the provider. A consumer must negotiate an offer successfully,
before its able to request data.

These two processes (offering & negotation) are documented in the [contracts.md](contracts.md)
