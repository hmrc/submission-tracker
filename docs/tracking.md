The Tracking object
----
  Fetch the tracking data for a tax identifier
  
* **URL**

  `/tracking/:id/:idType`

* **Method:**
  
  `GET`
  
*  **URL Params**

   **Required:**
 
   `id=[Nino,Utr]`
   
   The nino given must be a valid nino. ([http://www.hmrc.gov.uk/manuals/nimmanual/nim39110.htm](http://www.hmrc.gov.uk/manuals/nimmanual/nim39110.htm))

   `idType=[String]`

   The name of the tax identifier. i.e. nino or utr.

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 

        [Source...](https://https://github.com/hmrc/submission-tracker/tree/master/app/uk/gov/hmrc/submissiontracker/domain/Tracker.scala#L45)

```json
{
  [
    {
      "formId": "formId",
      "formName": "formName",
      "dfsSubmissionReference": "ref1",
      "businessArea": "some-business",
      "receivedDate": "20150801",
      "completionDate": "20150801",
      "milestones": [
        {
          "milestone": "one",
          "status": "open"
        }
      ]
    }
  ]
}
```
 
* **Error Response:**

  * **Code:** 400 BADREQUEST <br />
    **Content:** `{"code":"BAD_REQUEST","message":"Bad Request"}`

  * **Code:** 401 UNAUTHORIZED <br />
    **Content:** `{"code":"UNAUTHORIZED","message":"NINO does not exist on account"}`

  * **Code:** 406 NOT ACCEPTABLE <br />
    **Content:** `{"code":"ACCEPT_HEADER_INVALID","message":"The accept header is missing or invalid"}`

  OR when data cannot be resolved.

  * **Code:** 500 INTERNAL_SERVER_ERROR <br />



