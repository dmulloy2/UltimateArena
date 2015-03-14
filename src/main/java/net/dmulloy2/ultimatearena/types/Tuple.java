/**
 * (c) 2015 dmulloy2
 */
package net.dmulloy2.ultimatearena.types;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author dmulloy2
 */

@Data
@AllArgsConstructor
public class Tuple<A, B>
{
	private final A first;
	private final B second;
}