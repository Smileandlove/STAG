# STAG
## Description
This project aims to realize a simple text adventure game.
## Usage

```bash
$ java StagServer [entities.dot] [actions.json]
$ java StagClient [Username]
```

## Remarks

At the beginning I used ArrayList to store data. However, it was found that a large number of loops were written when identifying the command, which caused the entire code to be bloated. So I tried to rewrite the code using HashMap. I didn't refactor some of the methods seriously, which still seemed bloated and out of place. But because of time, this is the limit I can accomplish.

## Reference

[Briefing on STAG assignment](https://github.com/drslock/JAVA2020/tree/main/Weekly%20Workbooks/09%20Briefing%20on%20STAG%20assignment)

## Disclaimer

This work was submitted as coursework for the COMSM0086 Object Oriented Programming with Java module at the University of Bristol. Please note that no person can use this work without my permission or attempt to pass this work off as their own.
