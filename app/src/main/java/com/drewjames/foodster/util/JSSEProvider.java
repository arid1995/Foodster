package com.drewjames.foodster.util;
/*     http://www.apache.org/licenses/LICENSE-2.0
        *
        *  Unless required by applicable law or agreed to in writing, software
        *  distributed under the License is distributed on an "AS IS" BASIS,
        *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        *  See the License for the specific language governing permissions and
        *  limitations under the License.
        */

/**
 * @author Alexander Y. Kleymenov
 * @version $Revision$
 */


import java.security.AccessController;
import java.security.Provider;

public final class JSSEProvider extends Provider {

    public JSSEProvider() {
        super("HarmonyJSSE", 1.0, "Harmony JSSE Provider");
        AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
            public Void run() {
                put("SSLContext.TLS",
                        "org.apache.harmony.xnet.provider.jsse.SSLContextImpl");
                put("Alg.Alias.SSLContext.TLSv1", "TLS");
                put("KeyManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.KeyManagerFactoryImpl");
                put("TrustManagerFactory.X509",
                        "org.apache.harmony.xnet.provider.jsse.TrustManagerFactoryImpl");
                return null;
            }
        });
    }
}