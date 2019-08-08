/*
 * Copyright © 2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.cdap.common.metadata;

import com.google.common.base.Splitter;
import io.cdap.cdap.common.metadata.QueryTerm.Qualifier;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A thread-safe class that provides helper methods for metadata search string interpretation,
 * and defines search syntax for qualifying information (e.g. required terms) {@link QueryTerm.Qualifier}.
 */
public final class QueryParser {
  private static final Pattern SPACE_SEPARATOR_PATTERN = Pattern.compile("\\s+");
  private static final String KEYVALUE_SEPARATOR = ":";
  private static final char REQUIRED_OPERATOR = '+';

  // private constructor to prevent instantiation
  private QueryParser() {}

  /**
   * Organizes and separates a raw, space-separated search string
   * into multiple {@link QueryTerm} objects. Spaces are defined by the {@link QueryParser#SPACE_SEPARATOR_PATTERN}
   * field, the semantics of which are documented in Java's {@link Pattern} class.
   * Certain typical separations of terms, such as hyphens and commas, are not considered spaces.
   * This method preserves the original case of the query.
   *
   * This method supports the use of certain search operators that, when placed before a search term,
   * denote qualifying information about that search term. When translated into a QueryTerm object, search terms
   * containing an operator have the operator removed from the string representation.
   * The {@link QueryParser#REQUIRED_OPERATOR} character signifies a search term that must receive a match.
   * By default, this method considers search items without an operator to be optional.
   *
   * @param query the raw search string
   * @return a list of QueryTerms
   */
  public static List<QueryTerm> parse(String query) {
    List<QueryTerm> queryTerms = new ArrayList<>();
    for (String term : Splitter.on(SPACE_SEPARATOR_PATTERN)
        .omitEmptyStrings().trimResults().split(query)) {
      queryTerms.add(parseQueryTerm(term));
    }
    return queryTerms;
  }

  private static QueryTerm parseQueryTerm(String term) {
    if (term.charAt(0) == REQUIRED_OPERATOR && term.length() > 1) {
      if (term.substring(1).startsWith("DATE")) {
        return parseDateTerm(term.substring(1), Qualifier.REQUIRED);
      }
      return new QueryTerm(term.substring(1), Qualifier.REQUIRED);
    }
    if (term.startsWith("DATE")) {
      return parseDateTerm(term, Qualifier.REQUIRED);
    }
    return new QueryTerm(term, Qualifier.OPTIONAL);
  }

  /**
   * Parses a user's query when the "DATE" keyword is detected in the beginning of the query.
   * Extracts the comparison operator and converts the date string into a Unix timestamp and
   * creates a QueryTerm with this information.
   * If the date term cannot be converted to a Unix timestamp, creates a regular QueryTerm with the original term input.
   *
   * @param term      an individual term from the user's original query
   * @param qualifier the qualifier that is detected by queryParser
   * @return a date QueryTerm with the extracted information. If date query is invalid, returns a term-search QueryTerm
   */
  private static QueryTerm parseDateTerm(String term, Qualifier qualifier) {
    String valueTerm = lastSubTerm(term);
    String dateTerm = extractTermValue(valueTerm);
    Long date = parseDate(dateTerm);
    // If the user's date cannot be parsed then create a regular QueryTerm to search for the query as a string.
    // In this case assume that the DATE: keyword is a part of the user's intended string search.
    if (date == null) {
      return new QueryTerm(term, qualifier);
    }
    // Remove "DATE:" from the term.
    term = term.substring(5);

    if (term.contains(KEYVALUE_SEPARATOR)) {
      String[] split = term.split(KEYVALUE_SEPARATOR);
      if (split.length > 2) {
        //remove DATE: so that it is not considered as a field name.
        term = term.substring(5);
      }
    }
    return new QueryTerm(term, Qualifier.REQUIRED, QueryTerm.SearchType.DATE, findComparison(valueTerm), date);
  }

  /**
   * Parses a string into Unix timestamp if the string format is supported.
   *
   * @param term potential date string, with no additional syntax (fields, separators, or comparison operators)
   * @return Unix timestamp in local time as a Long, or null if the term cannot be parsed
   */
  public static Long parseDate(String term) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date parsedDate = sdf.parse(term, new ParsePosition(0));
    if (parsedDate == null) {
      return null;
    }
    return parsedDate.getTime();
  }

  // ALL OF THE FOLLOWING IS JORDAN'S CODE.
  // His code has not yet been merged with develop but I needed it for some functionality.
  // It is subject to change based on the results of his final PR merge.

  public static String extractTermValue(String term) {
    term = lastSubTerm(term);
    if (term.startsWith(">") || term.startsWith("<")) {
      term = term.substring(1);
    }
    if (term.startsWith("=")) {
      term = term.substring(1);
    }
    if (term.endsWith("*")) {
      term = term.substring(0, term.length() - 1);
    }
    return term;
  }

  private static String lastSubTerm(String term) {
    return term.substring(term.lastIndexOf(KEYVALUE_SEPARATOR) + 1);
  }

  private static QueryTerm.Comparison findComparison(String term) {
    term = lastSubTerm(term);
    if (term.startsWith(">=")) {
      return QueryTerm.Comparison.GREATER_OR_EQUAL;
    }
    if (term.startsWith(">")) {
      return QueryTerm.Comparison.GREATER;
    }
    if (term.startsWith("<=")) {
      return QueryTerm.Comparison.LESS_OR_EQUAL;
    }
    if (term.startsWith("<")) {
      return QueryTerm.Comparison.LESS;
    }

    return QueryTerm.Comparison.EQUAL;
  }

}
