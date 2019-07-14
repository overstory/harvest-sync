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

package harvest.csv

import org.supercsv.io.CsvListReader
import org.supercsv.io.CsvListWriter
import org.supercsv.io.ICsvListReader
import org.supercsv.io.ICsvListWriter
import org.supercsv.prefs.CsvPreference

/*
	This class reads a CSV exported from Harvest, which has a lot of unnecessary columns, and strips out all but the essential columns.
	This yields a file that is ready to be imported into another Harvest account.
 */
class HarvestExport
{
	private static CsvPreference inputPrefs()
	{
		return new CsvPreference.Builder ('"'.charAt (0), 44, '\r\n').build()
	}

	private static CsvPreference outputPrefs()
	{
		return new CsvPreference.Builder ('"'.charAt (0), 44, '\r\n').build()
	}

	static void main (String[] args) throws Exception
	{
		if (args.length < 2) {
			System.out.println ('usage: inputpath outputpath|-')
			return
		}

		ICsvListReader listReader = new CsvListReader (new FileReader (args [0]), inputPrefs())
		Writer out = ((args.length >= 2) && ( ! '-'.equals (args [1]))) ? new FileWriter (args [1]) : new OutputStreamWriter (System.out)
		ICsvListWriter listWriter = new CsvListWriter (out, outputPrefs())
		RowMapper clientMapper = new ColumnStripper()

		new CsvFilter (listReader, listWriter, clientMapper).run()
	}

	static class ColumnStripper implements RowMapper
	{
		private Map<String,Integer> columnMap = [:]
		private List<String> exportCols = ['Date', 'Client', 'Project', 'Task', 'Notes', 'Hours', 'First Name', 'Last Name']


		@Override
		String [] prepare (String [] headers)
		{
			headers.eachWithIndex { String entry, int index ->
				columnMap [entry] = index
			}

			exportCols.toArray()
		}

		@Override
		List<String> mapRow (List<String> columns)
		{
			List<String> row = []

			exportCols.each {
				row << columns [columnMap [it]]
			}

			row
		}
	}
}
