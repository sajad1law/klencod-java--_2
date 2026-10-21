import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TournamentManager extends JFrame {

    // الألوان والتصميم الداكن
    private static final Color NAVY_BG = new Color(10, 17, 40);
    private static final Color NAVY_PANEL = new Color(18, 31, 61);
    private static final Color PITCH_GREEN = new Color(39, 174, 96);
    private static final Color GOLD = new Color(241, 196, 15);
    private static final Color TEXT_LIGHT = new Color(226, 232, 240);
    
    private static final Font ARABIC_FONT = new Font("Tahoma", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Tahoma", Font.BOLD, 24);

    // قاعدة بيانات مصغرة للمنتخبات
    private static final List<Team> DB_TEAMS = Arrays.asList(
            new Team("ksa", "السعودية", 78, "🇸🇦"),
            new Team("irq", "العراق", 76, "🇮🇶"),
            new Team("qat", "قطر", 79, "🇶🇦"),
            new Team("uae", "الإمارات", 75, "🇦🇪"),
            new Team("oma", "عمان", 73, "🇴🇲"),
            new Team("bhr", "البحرين", 72, "🇧🇭"),
            new Team("kuw", "الكويت", 68, "🇰🇼"),
            new Team("yem", "اليمن", 60, "🇾🇪"),
            new Team("egy", "مصر", 79, "🇪🇬"),
            new Team("mar", "المغرب", 83, "🇲🇦"),
            new Team("alg", "الجزائر", 80, "🇩🇿"),
            new Team("tun", "تونس", 77, "🇹🇳"),
            new Team("arg", "الأرجنتين", 93, "🇦🇷"),
            new Team("fra", "فرنسا", 92, "🇫🇷"),
            new Team("bra", "البرازيل", 91, "🇧🇷"),
            new Team("jpn", "اليابان", 81, "🇯🇵")
    );

    private Tournament currentTournament;
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // الكلاسات الداخلية لتمثيل البيانات
    static class Team {
        String id, name, flag;
        int rating;
        public Team(String id, String name, int rating, String flag) {
            this.id = id; this.name = name; this.rating = rating; this.flag = flag;
        }
        @Override public String toString() { return flag + " " + name; }
    }

    static class Match {
        String id, stage, groupId;
        Team team1, team2;
        Integer score1, score2;
        boolean isPlayed, isSimulated;
        public Match(String id, String stage, String groupId, Team t1, Team t2) {
            this.id = id; this.stage = stage; this.groupId = groupId;
            this.team1 = t1; this.team2 = t2;
            this.isPlayed = false; this.isSimulated = false;
        }
    }

    static class Group {
        String id, name;
        List<Team> teams;
        public Group(String id, String name, List<Team> teams) {
            this.id = id; this.name = name; this.teams = teams;
        }
    }

    static class Tournament {
        String name;
        List<Team> teams;
        Team userTeam;
        List<Group> groups;
        List<Match> matches;
        public Tournament(String name, List<Team> teams, Team userTeam) {
            this.name = name; this.teams = teams; this.userTeam = userTeam;
            this.groups = new ArrayList<>();
            this.matches = new ArrayList<>();
        }
    }

    public TournamentManager() {
        setTitle("مدير البطولات الدولية");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        applyUITheme();

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);
        mainContainer.setBackground(NAVY_BG);
        
        mainContainer.add(createHomePanel(), "HOME");
        mainContainer.add(createWizardPanel(), "WIZARD");

        add(mainContainer);
        setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        applyOrientation(this);
    }

    private void applyUITheme() {
        UIManager.put("Panel.background", NAVY_BG);
        UIManager.put("Label.foreground", TEXT_LIGHT);
        UIManager.put("Button.font", ARABIC_FONT);
        UIManager.put("Label.font", ARABIC_FONT);
        UIManager.put("Table.font", ARABIC_FONT);
        UIManager.put("TableHeader.font", ARABIC_FONT);
        UIManager.put("TextField.font", ARABIC_FONT);
        UIManager.put("ComboBox.font", ARABIC_FONT);
        UIManager.put("OptionPane.messageFont", ARABIC_FONT);
        UIManager.put("OptionPane.buttonFont", ARABIC_FONT);
        UIManager.put("OptionPane.background", NAVY_BG);
        UIManager.put("Panel.background", NAVY_BG);
    }

    private void applyOrientation(Container c) {
        c.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        for (Component comp : c.getComponents()) {
            if (comp instanceof Container) {
                applyOrientation((Container) comp);
            }
        }
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(NAVY_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;

        JLabel title = new JLabel("مدير البطولات الدولية");
        title.setFont(new Font("Tahoma", Font.BOLD, 36));
        title.setForeground(GOLD);
        panel.add(title, gbc);

        gbc.gridy++;
        JButton btnNew = createStyledButton("إنشاء بطولة جديدة", PITCH_GREEN);
        btnNew.setPreferredSize(new Dimension(250, 50));
        btnNew.addActionListener(e -> cardLayout.show(mainContainer, "WIZARD"));
        panel.add(btnNew, gbc);

        return panel;
    }

    private JPanel createWizardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblTitle = new JLabel("إعداد البطولة", SwingConstants.CENTER);
        lblTitle.setFont(TITLE_FONT);
        lblTitle.setForeground(GOLD);
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        centerPanel.setBackground(NAVY_PANEL);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GOLD, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // اسم البطولة
        JPanel pnlName = new JPanel(new BorderLayout(10, 10));
        pnlName.setOpaque(false);
        pnlName.add(new JLabel("اسم البطولة:"), BorderLayout.EAST);
        JTextField txtName = new JTextField("كأس العالم الافتراضي");
        pnlName.add(txtName, BorderLayout.CENTER);
        centerPanel.add(pnlName);

        // المنتخبات
        JPanel pnlTeams = new JPanel(new BorderLayout(10, 10));
        pnlTeams.setOpaque(false);
        pnlTeams.add(new JLabel("اختر 8 منتخبات (مؤقتاً للنموذج):"), BorderLayout.EAST);
        JList<Team> lstTeams = new JList<>(new Vector<>(DB_TEAMS));
        lstTeams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lstTeams.setVisibleRowCount(4);
        pnlTeams.add(new JScrollPane(lstTeams), BorderLayout.CENTER);
        centerPanel.add(pnlTeams);

        // فريق المستخدم
        JPanel pnlUser = new JPanel(new BorderLayout(10, 10));
        pnlUser.setOpaque(false);
        pnlUser.add(new JLabel("اختر منتخبك (الذي ستلعب به):"), BorderLayout.EAST);
        JComboBox<Team> cmbUserTeam = new JComboBox<>(new Vector<>(DB_TEAMS));
        pnlUser.add(cmbUserTeam, BorderLayout.CENTER);
        centerPanel.add(pnlUser);

        panel.add(centerPanel, BorderLayout.CENTER);

        JButton btnStart = createStyledButton("بدء البطولة", PITCH_GREEN);
        btnStart.addActionListener(e -> {
            List<Team> selected = lstTeams.getSelectedValuesList();
            if(selected.size() < 4) {
                JOptionPane.showMessageDialog(this, "الرجاء اختيار 4 منتخبات على الأقل.");
                return;
            }
            Team userTeam = (Team) cmbUserTeam.getSelectedItem();
            if(!selected.contains(userTeam)) {
                JOptionPane.showMessageDialog(this, "منتخبك يجب أن يكون ضمن المنتخبات المشاركة.");
                return;
            }
            generateTournament(txtName.getText(), selected, userTeam);
        });
        panel.add(btnStart, BorderLayout.SOUTH);

        return panel;
    }

    private void generateTournament(String name, List<Team> selectedTeams, Team userTeam) {
        currentTournament = new Tournament(name, selectedTeams, userTeam);
        
        // خلط المنتخبات
        List<Team> shuffled = new ArrayList<>(selectedTeams);
        Collections.shuffle(shuffled);

        // تقسيم إلى مجموعتين (لتبسيط النموذج)
        int half = shuffled.size() / 2;
        Group g1 = new Group("G1", "المجموعة الأولى", shuffled.subList(0, half));
        Group g2 = new Group("G2", "المجموعة الثانية", shuffled.subList(half, shuffled.size()));
        currentTournament.groups.add(g1);
        currentTournament.groups.add(g2);

        // إنشاء مباريات المجموعات
        int matchId = 1;
        for (Group g : currentTournament.groups) {
            for (int i = 0; i < g.teams.size(); i++) {
                for (int j = i + 1; j < g.teams.size(); j++) {
                    currentTournament.matches.add(new Match("M" + (matchId++), "المجموعات", g.id, g.teams.get(i), g.teams.get(j)));
                }
            }
        }

        buildDashboard();
        cardLayout.show(mainContainer, "DASHBOARD");
    }

    private void buildDashboard() {
        // حذف الداشبورد القديم إن وجد
        Component[] comps = mainContainer.getComponents();
        for(Component c : comps) {
            if(c.getName() != null && c.getName().equals("DASHBOARD_PANEL")) {
                mainContainer.remove(c);
            }
        }

        JPanel dashPanel = new JPanel(new BorderLayout());
        dashPanel.setName("DASHBOARD_PANEL");
        dashPanel.setBackground(NAVY_BG);

        // الهيدر
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY_PANEL);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("🏆 " + currentTournament.name, SwingConstants.RIGHT);
        lblTitle.setFont(TITLE_FONT);
        lblTitle.setForeground(GOLD);
        
        JLabel lblUser = new JLabel("منتخبك: " + currentTournament.userTeam.toString(), SwingConstants.LEFT);
        lblUser.setFont(ARABIC_FONT);
        lblUser.setForeground(PITCH_GREEN);
        
        header.add(lblTitle, BorderLayout.EAST);
        header.add(lblUser, BorderLayout.WEST);
        dashPanel.add(header, BorderLayout.NORTH);

        // التبويبات
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(ARABIC_FONT);
        tabs.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        tabs.addTab("المباريات (الجولة الحالية)", createFixturesPanel());
        tabs.addTab("ترتيب المجموعات", createStandingsPanel());
        
        dashPanel.add(tabs, BorderLayout.CENTER);

        mainContainer.add(dashPanel, "DASHBOARD");
        applyOrientation(dashPanel);
    }

    private JPanel createFixturesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        for (Match m : currentTournament.matches) {
            listPanel.add(createMatchCard(m, listPanel));
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);

        // زر المحاكاة الجولة بجولة
        JButton btnSimulate = createStyledButton("محاكاة الجولة الحالية", GOLD);
        btnSimulate.setForeground(Color.BLACK);
        btnSimulate.addActionListener(e -> simulateNextRound(listPanel));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnSimulate);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void simulateNextRound(JPanel listPanel) {
        Set<String> teamsPlayedThisRound = new HashSet<>();
        boolean userMatchPending = false;
        boolean simulatedAny = false;

        for (Match m : currentTournament.matches) {
            if (m.isPlayed) continue;

            // التأكد أن الفريقين لم يلعبا في هذه الدفعة (محاكاة جولة واحدة)
            if (teamsPlayedThisRound.contains(m.team1.id) || teamsPlayedThisRound.contains(m.team2.id)) {
                continue;
            }

            // إذا كانت مباراة المستخدم، لا نحاكيها، وننبهه
            if (m.team1.id.equals(currentTournament.userTeam.id) || m.team2.id.equals(currentTournament.userTeam.id)) {
                userMatchPending = true;
                teamsPlayedThisRound.add(m.team1.id);
                teamsPlayedThisRound.add(m.team2.id);
                continue;
            }

            // محاكاة النتيجة
            int[] score = simulateScore(m.team1.rating, m.team2.rating);
            m.score1 = score[0];
            m.score2 = score[1];
            m.isPlayed = true;
            m.isSimulated = true;
            simulatedAny = true;

            teamsPlayedThisRound.add(m.team1.id);
            teamsPlayedThisRound.add(m.team2.id);
        }

        if (userMatchPending) {
            JOptionPane.showMessageDialog(this, "الجولة الحالية تتضمن مباراة لمنتخبك. يرجى إدخال النتيجة يدوياً.", "تنبيه", JOptionPane.INFORMATION_MESSAGE);
        } else if (!simulatedAny) {
            JOptionPane.showMessageDialog(this, "تم الانتهاء من جميع مباريات المجموعات.", "معلومة", JOptionPane.INFORMATION_MESSAGE);
        }

        refreshFixturesUI(listPanel);
        buildDashboard(); // تحديث الترتيب أيضاً
    }

    private int[] simulateScore(int rating1, int rating2) {
        Random r = new Random();
        int diff = rating1 - rating2;
        double baseGoals = 1.2;
        double lambda1 = Math.max(0.2, baseGoals + (diff * 0.05));
        double lambda2 = Math.max(0.2, baseGoals - (diff * 0.05));

        return new int[]{getPoisson(lambda1, r), getPoisson(lambda2, r)};
    }

    private int getPoisson(double lambda, Random r) {
        double L = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= r.nextDouble();
        } while (p > L);
        return k - 1;
    }

    private JPanel createMatchCard(Match m, JPanel listPanel) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(NAVY_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(m.isPlayed ? PITCH_GREEN : Color.DARK_GRAY, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(800, 80));

        boolean isUserMatch = m.team1.id.equals(currentTournament.userTeam.id) || m.team2.id.equals(currentTournament.userTeam.id);

        JLabel lblStage = new JLabel(m.stage + (m.isSimulated ? " (محاكاة)" : ""), SwingConstants.CENTER);
        lblStage.setForeground(Color.GRAY);
        lblStage.setPreferredSize(new Dimension(100, 30));

        JPanel centerInfo = new JPanel(new GridLayout(1, 3));
        centerInfo.setOpaque(false);
        
        JLabel t1 = new JLabel(m.team1.toString(), SwingConstants.LEFT);
        JLabel vs = new JLabel(m.isPlayed ? m.score1 + " - " + m.score2 : "VS", SwingConstants.CENTER);
        JLabel t2 = new JLabel(m.team2.toString(), SwingConstants.RIGHT);
        
        if(isUserMatch) {
            t1.setForeground(PITCH_GREEN); t2.setForeground(PITCH_GREEN);
        } else {
            t1.setForeground(TEXT_LIGHT); t2.setForeground(TEXT_LIGHT);
        }
        vs.setFont(new Font("Tahoma", Font.BOLD, 18));
        vs.setForeground(GOLD);

        centerInfo.add(t1); centerInfo.add(vs); centerInfo.add(t2);

        card.add(lblStage, BorderLayout.EAST);
        card.add(centerInfo, BorderLayout.CENTER);

        if (isUserMatch && !m.isPlayed) {
            JButton btnEnter = createStyledButton("إدخال النتيجة", PITCH_GREEN);
            btnEnter.setPreferredSize(new Dimension(120, 30));
            btnEnter.addActionListener(e -> {
                showScoreDialog(m);
                buildDashboard(); // إعادة رسم الشاشة بعد حفظ النتيجة
            });
            card.add(btnEnter, BorderLayout.WEST);
        } else {
            JLabel spacer = new JLabel("");
            spacer.setPreferredSize(new Dimension(120, 30));
            card.add(spacer, BorderLayout.WEST);
        }

        applyOrientation(card);
        return card;
    }

    private void showScoreDialog(Match m) {
        // تصميم مربع حوار يدعم مسح وكتابة الأرقام بسهولة بفضل استخدام JTextField بدلاً من JSpinner المعقد
        JPanel pnl = new JPanel(new GridLayout(2, 3, 10, 10));
        pnl.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pnl.setBackground(NAVY_BG);

        JLabel l1 = new JLabel(m.team1.name, SwingConstants.CENTER);
        l1.setForeground(Color.WHITE); l1.setFont(ARABIC_FONT);
        JLabel vs = new JLabel("-", SwingConstants.CENTER);
        vs.setForeground(GOLD); vs.setFont(TITLE_FONT);
        JLabel l2 = new JLabel(m.team2.name, SwingConstants.CENTER);
        l2.setForeground(Color.WHITE); l2.setFont(ARABIC_FONT);

        JTextField s1 = new JTextField(m.score1 != null ? String.valueOf(m.score1) : "");
        s1.setHorizontalAlignment(JTextField.CENTER);
        s1.setFont(TITLE_FONT);
        
        JTextField s2 = new JTextField(m.score2 != null ? String.valueOf(m.score2) : "");
        s2.setHorizontalAlignment(JTextField.CENTER);
        s2.setFont(TITLE_FONT);

        pnl.add(l1); pnl.add(vs); pnl.add(l2);
        pnl.add(s1); pnl.add(new JLabel("")); pnl.add(s2);

        int result = JOptionPane.showConfirmDialog(this, pnl, "إدخال نتيجة مباراتك", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int score1 = Integer.parseInt(s1.getText().trim());
                int score2 = Integer.parseInt(s2.getText().trim());
                if(score1 < 0 || score2 < 0) throw new NumberFormatException();
                
                m.score1 = score1;
                m.score2 = score2;
                m.isPlayed = true;
                m.isSimulated = false;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "الرجاء إدخال أرقام صحيحة وموجبة.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshFixturesUI(JPanel listPanel) {
        listPanel.removeAll();
        for (Match m : currentTournament.matches) {
            listPanel.add(createMatchCard(m, listPanel));
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createStandingsPanel() {
        JPanel panel = new JPanel(new GridLayout(currentTournament.groups.size(), 1, 20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setOpaque(false);

        for (Group g : currentTournament.groups) {
            panel.add(createGroupTable(g));
        }

        return new JScrollPane(panel);
    }

    private JPanel createGroupTable(Group g) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(NAVY_PANEL);
        pnl.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(GOLD),
            g.name, TitledBorder.RIGHT, TitledBorder.TOP, ARABIC_FONT, GOLD
        ));

        String[] cols = {"المركز", "المنتخب", "لعب", "فاز", "تعادل", "خسر", "له", "عليه", "فارق", "نقاط"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        List<TeamStats> stats = calculateStandings(g);
        int rank = 1;
        for (TeamStats s : stats) {
            model.addRow(new Object[]{
                rank++, s.team.toString(), s.pld, s.w, s.d, s.l, s.gf, s.ga, (s.gf - s.ga), s.pts
            });
        }

        JTable table = new JTable(model);
        table.setBackground(NAVY_PANEL);
        table.setForeground(TEXT_LIGHT);
        table.setGridColor(Color.DARK_GRAY);
        table.setRowHeight(30);
        table.getTableHeader().setBackground(NAVY_BG);
        table.getTableHeader().setForeground(GOLD);
        table.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        
        // تلوين فريق المستخدم
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String teamStr = (String) table.getValueAt(row, 1);
                if (teamStr.equals(currentTournament.userTeam.toString())) {
                    c.setBackground(new Color(39, 174, 96, 50));
                    c.setForeground(Color.WHITE);
                    c.setFont(new Font("Tahoma", Font.BOLD, 14));
                } else {
                    c.setBackground(NAVY_PANEL);
                    c.setForeground(TEXT_LIGHT);
                    c.setFont(ARABIC_FONT);
                }
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        applyOrientation(pnl);
        return pnl;
    }

    static class TeamStats {
        Team team;
        int pld=0, w=0, d=0, l=0, gf=0, ga=0, pts=0;
        public TeamStats(Team team) { this.team = team; }
    }

    private List<TeamStats> calculateStandings(Group g) {
        Map<String, TeamStats> map = new HashMap<>();
        for (Team t : g.teams) map.put(t.id, new TeamStats(t));

        for (Match m : currentTournament.matches) {
            if (m.isPlayed && m.groupId != null && m.groupId.equals(g.id)) {
                TeamStats s1 = map.get(m.team1.id);
                TeamStats s2 = map.get(m.team2.id);
                s1.pld++; s2.pld++;
                s1.gf += m.score1; s1.ga += m.score2;
                s2.gf += m.score2; s2.ga += m.score1;
                
                if (m.score1 > m.score2) { s1.w++; s1.pts+=3; s2.l++; }
                else if (m.score1 < m.score2) { s2.w++; s2.pts+=3; s1.l++; }
                else { s1.d++; s2.d++; s1.pts+=1; s2.pts+=1; }
            }
        }

        List<TeamStats> list = new ArrayList<>(map.values());
        list.sort((a, b) -> {
            if (b.pts != a.pts) return Integer.compare(b.pts, a.pts);
            int gdA = a.gf - a.ga, gdB = b.gf - b.ga;
            if (gdB != gdA) return Integer.compare(gdB, gdA);
            return Integer.compare(b.gf, a.gf);
        });
        return list;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new TournamentManager().setVisible(true);
        });
    }
}