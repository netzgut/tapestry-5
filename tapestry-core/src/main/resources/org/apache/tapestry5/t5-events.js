/* Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Defines the names of events used with the publish/subscribe framework.
 */
T5.define("events", {

    /**
     * Published as an element is being removed from the DOM, to allow framework-specific
     * approaches to removing any event listeners for the element. This is published on the document object,
     * and the message is the DOM element for which event handlers should be removed.
     */
    REMOVE_EVENT_HANDLERS : "tapestry:remove-event-handlers"

});