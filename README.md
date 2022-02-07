
# Import Voluntary Disclosure Frontend

## Purpose
This is the frontend service for the Import Voluntary Disclosure service. It provides user screens to allow users to make voluntary disclosure of issues they have noticed with their original import declarations.

## Running the service
### Service manager
The whole service can be started with:

`sm --start IVD_ALL -r`

or specifically for only the frontend

`sm --start IMPORT_VOLUNTARY_DISCLOSURE_FRONTEND -r`

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
