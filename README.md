ContactsNumberMapper
============

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


License
-------

    Copyright 2018 KushtrimPacaj

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
