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
   
    **Required:**
      
    `journeyId=[String]`
   
    a string which is included for journey tracking purposes but has no functional impact
 

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** 

        [Source...](https://https://github.com/hmrc/submission-tracker/tree/master/app/uk/gov/hmrc/submissiontracker/domain/Tracker.scala#L45)

```json
{
  "submissions": [
    {
      "formId": "R39_EN",
      "formName": "Claim a tax refund",
      "dfsSubmissionReference": "111-ABCD-456",
      "businessArea": "PSA",
      "receivedDate": "20150412",
      "completionDate": "20150517",
      "milestone": "InProgress",
      "milestones": [
        {
          "milestone": "Received",
          "status": "complete"
        },
        {
          "milestone": "Acquired",
          "status": "complete"
        },
        {
          "milestone": "InProgress",
          "status": "current"
        },
        {
          "milestone": "Done",
          "status": "incomplete"
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



