/*******************************************************************************
 * (c) Copyright IBM Corporation 2017.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package wasdev.ejb.ejb;

import wasdev.ejb.local.DiceLocal;

import javax.ejb.Local;
import javax.ejb.Stateless;
import java.util.Random;

@Stateless
@Local(DiceLocal.class)
public class DiceBean implements DiceLocal {

	@Deprecated
	public String hello() {
		return "Hello EJB World.";
	}

	public int rollDice(int sides, int quantity) {
		Random random = new Random();
		int result = 0;
		for (int i = 0; i < quantity; i++) {
			result += random.nextInt(sides) + 1;
		}
		return result;
	}
}
