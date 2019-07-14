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

/*
	This class composes the two operations done by the classes FilterHarvestTime and HarvestExport so that it
	can read a CSV exported from someone else's Harvest, map the client names and strip the unneeded columns
	so that it's ready to import into your Harvest.
 */
class MapAndStrip
{
/*
 * Program arguments:
 * Arg 0: Input CSV file name
 * Arg 1: Output CSV file name, or "-" for stdout
 * Arg 2: Comma-separated list of project=client pairs, ie: Sapphire=Emerald Publishing,Nisto Link=Social Awareness
 *
 * JVM system property settings
 * -Dcsv-input-delimiter=<char>, default is comma, could also be pipe char, etc
 * -Dcsv-output-delimiter=<char>, default is comma, could also be pipe char, etc
 * -Dcsv-input-quote=<char>, default is "
 * -Dcsv-output-quote=<char>, default is "
 */

	static void main (String[] args)
	{
		if (args.length < 3) {
			System.out.println ("usage: inputpath outputpath|- project-client-map")
			return
		}

		ICsvListReader listReader = new CsvListReader (new FileReader (args [0]), FilterHarvestTime.inputPrefs())
		CharArrayWriter caw = new CharArrayWriter()
		ICsvListWriter listWriter = new CsvListWriter (caw, FilterHarvestTime.outputPrefs())
		RowMapper clientMapper = new ClientMapper (FilterHarvestTime.clientMap (args[2]))

		new CsvFilter (listReader, listWriter, clientMapper).run()


		listReader = new CsvListReader (new CharArrayReader(caw.toCharArray()), FilterHarvestTime.inputPrefs())
		Writer out = ((args.length >= 2) && ( ! '-'.equals (args [1]))) ? new FileWriter (args [1]) : new OutputStreamWriter (System.out)
		listWriter = new CsvListWriter (out, FilterHarvestTime.outputPrefs())
		clientMapper = new HarvestExport.ColumnStripper()

		new CsvFilter (listReader, listWriter, clientMapper).run()
	}
}
