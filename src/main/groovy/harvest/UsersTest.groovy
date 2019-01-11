/*
 *  Copyright 2019 OverStory Ltd <copyright@overstory.co.uk> and other contributors
 *  (see the CONTRIBUTORS file).
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package harvest

import org.apache.http.HttpResponse

class UsersTest
{
	static final String URL = 'https://api.harvestapp.com/v2/users'

	static void main (String[] args)
	{
		RequestRunner rr = new RequestRunner (token: System.getProperty ('token'), accountId: System.getProperty ('accountid'))
		HttpResponse resp = rr.doRequest (URL)

		if (resp.statusLine.statusCode == 200) {
			println (resp.entity.content.text)
		} else {
			println ("Error: code=${resp.statusLine.statusCode}, ${resp.entity.content.text}")
		}
	}
}
