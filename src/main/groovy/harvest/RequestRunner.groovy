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
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients

class RequestRunner
{
	static final String DEFAULT_USER_AGENT = 'OverStory API Test (ron@overstory.co.uk)'

	String userAgent = DEFAULT_USER_AGENT
	String token
	String accountId

	HttpResponse doRequest (String url)
	{
		CloseableHttpClient httpclient = HttpClients.createDefault()
		HttpGet httpGet = new HttpGet (url)

		httpGet.addHeader ('Accept', 'application/json')
		httpGet.addHeader ('User-Agent', userAgent)
		httpGet.addHeader ('Authorization', "Bearer ${System.getProperty ('token')}")
		httpGet.addHeader ('Harvest-Account-ID', System.getProperty ('accountid'))

		httpclient.execute (httpGet)
	}
}
