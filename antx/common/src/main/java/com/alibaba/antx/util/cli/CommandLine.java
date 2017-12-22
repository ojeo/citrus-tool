/*
 * Copyright (c) 2002-2012 Alibaba Group Holding Limited.
 * All rights reserved.
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
 */

package com.alibaba.antx.util.cli;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Represents list of arguments parsed against a {@link Options} descriptor.
 * <p>
 * <p>
 * It allows querying of a boolean {@link #hasOption(String opt)}, in addition
 * to retrieving the {@link #getOptionValue(String opt)} for options requiring
 * arguments.
 * </p>
 * <p>
 * Additionally, any left-over or unrecognized arguments, are available for
 * further processing.
 * </p>
 *
 * @author bob mcwhirter (bob @ werken.com)
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author John Keyes (john at integralsource.com)
 */
public class CommandLine {

    /** the unrecognised options/arguments */
    private List args = new LinkedList();

    /** the processed options */
    private Map options = new HashMap();

    /** Map of unique options for ease to get complete list of options */
    private Map hashcodeMap = new HashMap();

    /** the processed options */
    private Option[] optionsArray;

    /**
     * <p>
     * Creates a command line.
     * </p>
     */
    CommandLine() {
    }

    /**
     * <p>
     * Query to see if an option has been set.
     * </p>
     *
     * @param opt Short name of the option
     * @return true if set, false if not
     */
    public boolean hasOption(String opt) {
        return options.containsKey(opt);
    }

    /**
     * <p>
     * Query to see if an option has been set.
     * </p>
     *
     * @param opt character name of the option
     * @return true if set, false if not
     */
    public boolean hasOption(char opt) {
        return hasOption(String.valueOf(opt));
    }

    /**
     * <p>
     * Return the <code>Object</code> type of this <code>Option</code>.
     * </p>
     *
     * @param opt the name of the option
     * @return the type of this <code>Option</code>
     */
    public Object getOptionObject(String opt) {
        String res = getOptionValue(opt);

        Object type = ((Option) ((List) options.get(opt)).iterator().next()).getType();
        return res == null ? null : TypeHandler.createValue(res, type);
    }

    /**
     * <p>
     * Return the <code>Object</code> type of this <code>Option</code>.
     * </p>
     *
     * @param opt the name of the option
     * @return the type of opt
     */
    public Object getOptionObject(char opt) {
        return getOptionObject(String.valueOf(opt));
    }

    /**
     * <p>
     * Retrieve the argument, if any, of this option.
     * </p>
     *
     * @param opt the name of the option
     * @return SimpleValue of the argument if option is set, and has an
     *         argument, otherwise null.
     */
    public String getOptionValue(String opt) {
        String[] values = getOptionValues(opt);
        return values == null ? null : values[0].trim();
    }

    /**
     * <p>
     * Retrieve the argument, if any, of this option.
     * </p>
     *
     * @param opt the character name of the option
     * @return SimpleValue of the argument if option is set, and has an
     *         argument, otherwise null.
     */
    public String getOptionValue(char opt) {
        return getOptionValue(String.valueOf(opt));
    }

    /**
     * <p>
     * Retrieves the array of values, if any, of an option.
     * </p>
     *
     * @param opt string name of the option
     * @return Values of the argument if option is set, and has an argument,
     *         otherwise null.
     */
    public String[] getOptionValues(String opt) {
        List values = new java.util.ArrayList();

        if (options.containsKey(opt)) {
            List opts = (List) options.get(opt);
            Iterator iter = opts.iterator();

            while (iter.hasNext()) {
                Option optt = (Option) iter.next();
                values.addAll(optt.getValuesList());
            }
        }
        return values.size() == 0 ? null : (String[]) values.toArray(new String[] { });
    }

    /**
     * <p>
     * Retrieves the array of values, if any, of an option.
     * </p>
     *
     * @param opt character name of the option
     * @return Values of the argument if option is set, and has an argument,
     *         otherwise null.
     */
    public String[] getOptionValues(char opt) {
        return getOptionValues(String.valueOf(opt));
    }

    /**
     * <p>
     * Retrieve the argument, if any, of an option.
     * </p>
     *
     * @param opt          name of the option
     * @param defaultValue is the default value to be returned if the option is
     *                     not specified
     * @return SimpleValue of the argument if option is set, and has an
     *         argument, otherwise <code>defaultValue</code>.
     */
    public String getOptionValue(String opt, String defaultValue) {
        String answer = getOptionValue(opt);
        return answer != null ? answer : defaultValue;
    }

    /**
     * <p>
     * Retrieve the argument, if any, of an option.
     * </p>
     *
     * @param opt          character name of the option
     * @param defaultValue is the default value to be returned if the option is
     *                     not specified
     * @return SimpleValue of the argument if option is set, and has an
     *         argument, otherwise <code>defaultValue</code>.
     */
    public String getOptionValue(char opt, String defaultValue) {
        return getOptionValue(String.valueOf(opt), defaultValue);
    }

    /**
     * <p>
     * Retrieve any left-over non-recognized options and arguments
     * </p>
     *
     * @return remaining items passed in but not parsed as an array
     */
    public String[] getArgs() {
        String[] answer = new String[args.size()];
        args.toArray(answer);
        return answer;
    }

    /**
     * <p>
     * Retrieve any left-over non-recognized options and arguments
     * </p>
     *
     * @return remaining items passed in but not parsed as a <code>List</code>.
     */
    public List getArgList() {
        return args;
    }

    /**
     * jkeyes - commented out until it is implemented properly
     * <p>
     * Dump state, suitable for debugging.
     * </p>
     *
     * @return Stringified form of this object
     */
    /*
     * public String toString() { StringBuffer buf = new StringBuffer();
     * buf.append( "[ CommandLine: [ options: " ); buf.append(
     * options.toString() ); buf.append( " ] [ args: "); buf.append(
     * args.toString() ); buf.append( " ] ]" ); return buf.toString(); }
     */

    /**
     * <p>
     * Add left-over unrecognized option/argument.
     * </p>
     *
     * @param arg the unrecognised option/argument.
     */
    public void addArg(String arg) {
        args.add(arg);
    }

    /**
     * <p>
     * Add an option to the command line. The values of the option are stored.
     * </p>
     *
     * @param opt the processed option
     */
    public void addOption(Option opt) {
        hashcodeMap.put(new Integer(opt.hashCode()), opt);

        String key = opt.getOpt();
        if (" ".equals(key)) {
            key = opt.getLongOpt();
        }

        if (options.get(key) != null) {
            ((java.util.List) options.get(key)).add(opt);
        } else {
            options.put(key, new java.util.ArrayList());
            ((java.util.List) options.get(key)).add(opt);
        }
    }

    /**
     * <p>
     * Returns an iterator over the Option members of CommandLine.
     * </p>
     *
     * @return an <code>Iterator</code> over the processed {@link Option}
     *         members of this {@link CommandLine}
     */
    public Iterator iterator() {
        return hashcodeMap.values().iterator();
    }

    /**
     * <p>
     * Returns an array of the processed {@link Option}s.
     * </p>
     *
     * @return an array of the processed {@link Option}s.
     */
    public Option[] getOptions() {
        Collection processed = hashcodeMap.values();

        // reinitialise array
        optionsArray = new Option[processed.size()];

        // return the array
        return (Option[]) processed.toArray(optionsArray);
    }
}
