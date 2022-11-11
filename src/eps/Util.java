/*
 *  Andre Cavalcante e Rafael Mendonca
 *  Copyright UFAM 2015-2016
 */
package eps;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Rafael
 */
public final class Util {

    private Util() {
    }

    public static SkillTemplate fromSkill(Skill skill) {
        SkillTemplate st = new SkillTemplate();
        st.name = skill.getName();
        st.argsTypes = skill.getArgsTypes();
        st.resultType = skill.getResultType();
        for (String props : skill.getProperties()) {
            st.properties.put(SkillBase.getPropName(props), SkillBase.getPropValue(props));
        }
        return st;
    }

    public static SkillTemplate[] fromSkill(Skill[] skills) {
        SkillTemplate[] sts = new SkillTemplate[skills.length];
        for (int i = 0; i < skills.length; i++) {
            sts[i] = Util.fromSkill(skills[i]);
        }
        return sts;
    }

    public static class JTextAreaOutputStream extends OutputStream {

        private final JTextArea destination;
        
        public JTextAreaOutputStream(JTextArea destination) {
            if (destination == null) {
                throw new IllegalArgumentException("Destination is null");
            }

            this.destination = destination;
        }

        @Override
        public void write(byte[] buffer, int offset, int length) throws IOException {
            final String text = new String(buffer, offset, length);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    destination.append(text);
                }
            });
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b}, 0, 1);
        }
    }

}
