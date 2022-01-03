# CLI DataLoader

## apply

Creates and updates contract definitions and/or assets.

__Usage__

```bash
$ edc apply (-f FILENAME | -k DIRECTORY)
```

__Flags__

| Name    | Default | Usage                                        |
|:--------|:--------|:---------------------------------------------|
| dry-run | false   | If true only prints the objects that change. |
| output  | json    | Output format (json, tsv, csv, or none).     |

## delete

Deletes contract definitions and/or assets.

__Usage__

```bash
$ edc delete (-f FILENAME | -k DIRECTORY)
```

__Flags__

| Name    | Default | Usage                                        |
|:--------|:--------|:---------------------------------------------|
| dry-run | false   | If true only prints the objects that change. |
| output  | json    | Output format (json, tsv, csv, or none).     |

## create

Creates contract definitions and/or assets from a file or stdin.

__Usage__

```bash
$ edc create (-f FILENAME | -k DIRECTORY)
```

__Flags__

| Name    | Default | Usage                                        |
|:--------|:--------|:---------------------------------------------|
| dry-run | false   | If true only prints the objects that change. |
| output  | json    | Output format (json, tsv, csv, or none).     |

### asset

Creates an asset.

__Usage__

```bash
$ edc create asset ID 
```

TBD

## get

TBD
