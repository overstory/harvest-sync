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

import org.supercsv.io.ICsvListReader
import org.supercsv.io.ICsvListWriter



/**
 * Created by IntelliJ IDEA.
 * User: ron
 * Date: 7/11/18
 * Time: 11:13 PM
 *
 */
@SuppressWarnings ("ClassWithoutNoArgConstructor")
class CsvFilter
{
	private final ICsvListReader listReader
	private final ICsvListWriter listWriter
	private final RowMapper rowMapper

	CsvFilter (ICsvListReader listReader, ICsvListWriter listWriter, RowMapper rowMapper)
	{
		this.listReader = listReader
		this.listWriter = listWriter
		this.rowMapper = rowMapper
	}

	CsvFilter (ICsvListReader listReader, ICsvListWriter listWriter)
	{
		this (listReader, listWriter, null)
	}


	void run() throws IOException
	{
		String [] header = listReader.getHeader (true)

		long columnCount = header.length
		long rowNumber = 0
		long badRows = 0
		long goodRows = 0

		listWriter.write (header)
		List<String> row

		rowMapper.prepare (header)

		try {
			while ((row = listReader.read()) != null)
			{
				rowNumber++

				if (row.size() != columnCount) {
					System.err.println ("** Row " + rowNumber + " has " + row.size() + " columns, expected " + columnCount + " (" + row.get (0) + "), skipping")
					badRows++
				}

				rowMapper.mapRow (row)

				goodRows++

				listWriter.write (row)
			}
		} finally {
			if (badRows != 0) {
				System.err.println ("Found " + badRows + " malformed rows")
			}

			listReader.close()
			listWriter.flush()
			listWriter.close()

			System.err.println ("Wrote header + " + goodRows + " rows")
		}
	}
}
