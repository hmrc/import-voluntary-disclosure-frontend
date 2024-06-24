
# Import Voluntary Disclosure Frontend

## Purpose
This is the frontend service for the Import Voluntary Disclosure service. It provides user screens to allow users to make voluntary disclosure of issues they have noticed with their original import declarations.

Other related C18 services:
- Backend service: [Import-Voluntary-Disclosure-Submission](https://github.com/hmrc/import-voluntary-disclosure-submission)
- Stub: [Import-Voluntary-Disclosure-Stub](https://github.com/hmrc/import-voluntary-disclosure-stub)

## Running the service
### Service manager
The whole service can be started with:

`sm2 --start IVD_ALL`

or specifically for only the frontend

`sm2 --start IMPORT_VOLUNTARY_DISCLOSURE_FRONTEND`

### Locally
`sbt run` or `sbt 'run 7950'`

To access the service locally http://localhost:7950/disclose-import-taxes-underpayment

## Downstream services
The following downstream services are used:

* **Address Lookup Frontend**        
  used to gather importer and user address details

* **Upscan**              
  used to allow the upload of files evidencing their disclosure submissions

* **Import Voluntary Disclosure Submission**              
  used to access off-platform endpoint for SUB09 and Create/Update Case

### Formatting code
This library uses [Scalafmt](https://scalameta.org/scalafmt/), a code formatter for Scala. The formatting rules configured for this repository are defined within [.scalafmt.conf](.scalafmt.conf). Prior to checking in any changes to this repository, please make sure all files are formatted correctly.

To apply formatting to this repository using the configured rules in [.scalafmt.conf](.scalafmt.conf) execute:

```
sbt scalafmtAll
```

To check files have been formatted as expected execute:

```
sbt scalafmtCheckAll scalafmtSbtCheck
```
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
