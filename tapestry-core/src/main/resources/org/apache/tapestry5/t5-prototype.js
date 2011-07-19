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
 * Adapts Tapestry's SPI (Service Provider Interface) to make use of the
 * Prototype JavaScript library. May also make modifications to Prototype to
 * work with Tapestry.
 */
T5.extend(T5.spi, function() {

    document.observe("dom:loaded", function() {
        T5.sub(T5.events.REMOVE_EVENT_HANDLERS, null, function(element) {
                Event.stopObserving(element);
            }
        );
    });

    return {};
});

