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

class FilterHarvestTime
{
	private static CsvPreference inputPrefs()
	{
		return new CsvPreference.Builder ('"'.charAt (0), 44, '\r\n').build()
	}

	private static CsvPreference outputPrefs()
	{
		return new CsvPreference.Builder ('"'.charAt (0), 44, '\r\n').build()
	}

	private static Map<String,String> clientMap (String mapList)
	{
		Map<String,String> map = new HashMap<>()
		String [] pairs = mapList.split (',')

		pairs.each {
			String [] nv = it.split ('=')

			if (nv.length != 2) throw new IllegalStateException ("Malformed name/value pair: ${it}")

			map.put (nv [0], nv [1])
		}

		if (map.size () == 0) throw new IllegalStateException ("No project->client mappings provided")

		map
	}


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
	static void main (String[] args) throws Exception
	{
		if (args.length < 3) {
			System.out.println ("usage: inputpath outputpath|- project-client-map");
			return;
		}

		ICsvListReader listReader = new CsvListReader (new FileReader (args [0]), inputPrefs())
		Writer out = ((args.length >= 2) && ( ! '-'.equals (args [1]))) ? new FileWriter (args [1]) : new OutputStreamWriter (System.out)
		ICsvListWriter listWriter = new CsvListWriter (out, outputPrefs())
		RowMapper clientMapper = new ClientMapper (clientMap (args[2]))

		new CsvFilter (listReader, listWriter, clientMapper).run()
	}

}

// ------------------------------------------------------------------------------------

class ClientMapper implements RowMapper
{
	private Map<String,String> projectMap
	private int clientColumn = -1
	private int projectColumn = -1

	ClientMapper (Map<String,String> projectMap)
	{
		this.projectMap = projectMap
	}

	@Override
	void prepare (String [] headers)
	{
		headers.eachWithIndex { String entry, int i ->
			if ('Project' == headers [i]) projectColumn = i
			if ('Client' == headers [i]) clientColumn = i
		}

		if (clientColumn == -1) throw new IllegalStateException("Could not find 'Client' column")
		if (projectColumn == -1) throw new IllegalStateException("Could not find 'Project' column")
	}

	@Override
	String[] mapRow (List<String> columns)
	{
		String project = columns [projectColumn]
		String newClient = projectMap [project]

		if (newClient == null) throw new IllegalStateException ("Cannot map project '${project}' to new client")

		columns [clientColumn] = newClient
	}
}
