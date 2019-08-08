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

import java.util.Objects;

/**
 * Represents a single item in a search query in terms of its content (i.e. the value being searched for)
 * and its qualifying information (e.g. whether a match for it is optional or required).
 * Is typically constructed in a list via {@link QueryParser#parse(String)}
 */
public class QueryTerm {
  private final String term;
  private final Qualifier qualifier;
  private final SearchType searchType;
  private final Comparison comparison;
  private final Long date;

  /**
   * Defines the different types of search terms that can be input.
   * A qualifier determines how the search implementation should handle the given term, e.g.
   * prioritizing required terms over optional ones.
   */
  public enum Qualifier {
    OPTIONAL, REQUIRED
  }

  /**
   * Defines the type of search that should be implemented for this term.
   * The default type is a STANDARD search.
   */
  public enum SearchType {
    STANDARD, DATE
  }

  /**
   * Defines the possible comparison operators for numeric and date values.
   */
  public enum Comparison {
    GREATER, LESS, EQUAL, GREATER_OR_EQUAL, LESS_OR_EQUAL
  }

  /**
   * Constructs a QueryTerm using the search term and its qualifying information.
   * searchType is assumed to be standard.
   *
   * @param term the search term
   * @param qualifier the qualifying information {@link Qualifier}
   */
  public QueryTerm(String term, Qualifier qualifier) {
    this.term = term;
    this.qualifier = qualifier;
    this.searchType = SearchType.STANDARD;
    this.comparison = Comparison.EQUAL;
    this.date = null;
  }

  /**
   * Constructs a QueryTerm using the search term and its qualifying and type information.
   *
   * @param term the search term
   * @param qualifier the qualifying information {@link Qualifier}
   * @param searchType the search method information {@link SearchType}
   */
  public QueryTerm(String term, Qualifier qualifier, SearchType searchType) {
    this.term = term;
    this.qualifier = qualifier;
    this.searchType = searchType;
    this.comparison = Comparison.EQUAL;
    this.date = null;
  }

  /**
   * Constructs a QueryTerm using search term, qualifying, type, and numeric information.
   *
   * @param term the search term
   * @param qualifier the qualifying information {@link Qualifier}
   * @param searchType the search method information {@link SearchType}
   * @param comparison the comparison operator for the numeric value {@link Comparison}
   * @param number the number which can be associated with a date or a numeric value, depending on the search type
   */
  public QueryTerm(String term, Qualifier qualifier, SearchType searchType, Comparison comparison, Long number) {
    this.term = term;
    this.qualifier = qualifier;
    this.searchType = searchType;
    this.comparison = comparison;
    if (this.searchType.equals(SearchType.DATE)) {
      this.date = number;
    } else {
      this.date = null;
    }
  }

  /**
   * @return the search term, without its preceding operator
   */
  public String getTerm() {
    return term;
  }

  /**
   * @return the search term's qualifying information
   */
  public Qualifier getQualifier() {
    return qualifier;
  }

  /**
   * @return the search term's search type
   */
  public SearchType getSearchType() {
    return searchType;
  }

  /**
   * @return the query term's comparison operator
   */
  public Comparison getComparison() {
    return comparison;
  }

  /**
   * @return the query term's date value
   */
  public Long getDate() {
    return date;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    QueryTerm that = (QueryTerm) o;

    return Objects.equals(term, that.getTerm()) && Objects.equals(qualifier, that.getQualifier());
  }

  @Override
  public int hashCode() {
    return Objects.hash(term, qualifier);
  }
}
