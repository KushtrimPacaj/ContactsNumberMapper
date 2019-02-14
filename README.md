# ContactsNumberMapper

Barebones, crappy-UI, but works.
Parses numbers that begin with specific codes, and adds a new number that begins with +383. Intentionally doesn't delete the old one.


```kotlin
private fun makeMagic(contactArrayList: ArrayList<KContact>) {
        for (kContact in contactArrayList) {

            for (phoneNumber in kContact.phoneNumbers) {

                val codesToConvert = arrayOf("+377", "00377", "+386", "00386", "+381", "00381")

                codesToConvert.forEach {
                    modifyCountryCode(phoneNumber, kContact, oldCode = it, newCode = "+383")

                }
            }
        }
    }
```
