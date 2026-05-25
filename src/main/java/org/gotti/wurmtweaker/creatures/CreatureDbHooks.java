package org.gotti.wurmtweaker.creatures;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CreatureDbHooks {

    // Called from injected bytecode — must be public static with no checked exceptions.
    public static long safeGetTemplateId(ResultSet rs) {
        try {
            return rs.getLong("TEMPLATEID");
        } catch (SQLException e) {
            return -1L;
        }
    }

    // Called from injected bytecode — must be public static with no checked exceptions.
    // Returns null if the template ID is not found, so callers can fall back to name lookup.
    public static com.wurmonline.server.creatures.CreatureTemplate tryGetTemplateById(
            com.wurmonline.server.creatures.CreatureTemplateFactory factory, int id) {
        try {
            return factory.getTemplate(id);
        } catch (Exception e) {
            return null;
        }
    }

    public static void register() {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        try {
            registerWriteHook(classPool);
            registerBulkReadHook(classPool);
            registerIndividualReadHook(classPool);
        } catch (NotFoundException | CannotCompileException e) {
            throw new HookException(e);
        }
    }

    // After saveCreatureStatus() succeeds, backfill TEMPLATEID for newly created rows.
    // The WHERE TEMPLATEID=-1 condition makes this idempotent — runs once per row, then no-ops.
    private static void registerWriteHook(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass ctDbCreatureStatus = classPool.get("com.wurmonline.server.creatures.DbCreatureStatus");
        CtMethod saveMethod = ctDbCreatureStatus.getDeclaredMethod("saveCreatureStatus");
        saveMethod.insertAfter(
            "if ($_) {" +
            "  try {" +
            "    java.sql.Connection _c = com.wurmonline.server.DbConnector.getCreatureDbCon();" +
            "    try {" +
            "      java.sql.PreparedStatement _ps = _c.prepareStatement(" +
            "          \"UPDATE CREATURES SET TEMPLATEID=? WHERE WURMID=? AND TEMPLATEID=-1\");" +
            "      try {" +
            "        _ps.setInt(1, this.template.getTemplateId());" +
            "        _ps.setLong(2, $1);" +
            "        _ps.executeUpdate();" +
            "      } finally { _ps.close(); }" +
            "    } finally { com.wurmonline.server.DbConnector.returnConnection(_c); }" +
            "  } catch (java.sql.SQLException _ex) {" +
            "    java.util.logging.Logger.getLogger(\"WurmTweaker\").warning(" +
            "        \"WurmTweaker: failed to set TEMPLATEID for WURMID=\" + $1 + \": \" + _ex.getMessage());" +
            "  }" +
            "}"
        );
    }

    // Bulk creature load at startup: prefer TEMPLATEID lookup over TEMPLATENAME string lookup.
    // Falls back to the original string lookup for rows where TEMPLATEID is still -1.
    private static void registerBulkReadHook(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass ctCreatures = classPool.get("com.wurmonline.server.creatures.Creatures");
        CtMethod initMethod = ctCreatures.getDeclaredMethod("initializeCreature");
        initMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplateFactory")
                        && m.getMethodName().equals("getTemplate")) {
                    m.replace(
                        "{" +
                        "  long _tid = rs.getLong(\"TEMPLATEID\");" +
                        "  if (_tid != -1L) {" +
                        "    com.wurmonline.server.creatures.CreatureTemplate _tmpl =" +
                        "        org.gotti.wurmtweaker.creatures.CreatureDbHooks.tryGetTemplateById($0, (int) _tid);" +
                        "    if (_tmpl != null) {" +
                        "      $_ = _tmpl;" +
                        "    } else {" +
                        "      $_ = $proceed($$);" +
                        "    }" +
                        "  } else {" +
                        "    $_ = $proceed($$);" +
                        "  }" +
                        "}"
                    );
                }
            }
        });
    }

    // Individual creature load (offline creatures, players): same TEMPLATEID-first logic.
    // PLAYERS table has no TEMPLATEID column, so getLong is wrapped via safeGetTemplateId.
    private static void registerIndividualReadHook(ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass ctDbCreatureStatus = classPool.get("com.wurmonline.server.creatures.DbCreatureStatus");
        CtMethod loadMethod = ctDbCreatureStatus.getDeclaredMethod("load");
        loadMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("com.wurmonline.server.creatures.CreatureTemplateFactory")
                        && m.getMethodName().equals("getTemplate")) {
                    m.replace(
                        "{" +
                        "  long _tid = org.gotti.wurmtweaker.creatures.CreatureDbHooks.safeGetTemplateId(rs);" +
                        "  if (_tid != -1L) {" +
                        "    com.wurmonline.server.creatures.CreatureTemplate _tmpl =" +
                        "        org.gotti.wurmtweaker.creatures.CreatureDbHooks.tryGetTemplateById($0, (int) _tid);" +
                        "    if (_tmpl != null) {" +
                        "      $_ = _tmpl;" +
                        "    } else {" +
                        "      $_ = $proceed($$);" +
                        "    }" +
                        "  } else {" +
                        "    $_ = $proceed($$);" +
                        "  }" +
                        "}"
                    );
                }
            }
        });
    }
}
