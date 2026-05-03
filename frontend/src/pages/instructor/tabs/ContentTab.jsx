import { useCallback, useEffect, useMemo, useState } from 'react';
import LessonsTree from '../../../components/instructor/LessonsTree.jsx';
import LessonEditor from '../../../components/instructor/LessonEditor.jsx';
import {
    getModules,
    createModule as apiCreateModule,
    createLesson as apiCreateLesson,
    updateLesson as apiUpdateLesson,
} from '../../../api.js';

export default function ContentTab({ course }) {
    const courseId = course?.id;
    const [modules, setModules] = useState([]);
    const [activeLessonId, setActiveLessonId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState(null);

    const fetchModules = useCallback(async () => {
        if (!courseId) return;
        setLoading(true);
        setError(null);
        try {
            const data = await getModules(courseId);
            setModules(data);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to load modules');
        } finally {
            setLoading(false);
        }
    }, [courseId]);

    useEffect(() => {
        fetchModules();
    }, [fetchModules]);

    const activeLesson = useMemo(() => {
        for (const m of modules) {
            const l = m.lessons?.find((x) => x.id === activeLessonId);
            if (l) return { ...l, moduleId: m.id };
        }
        return null;
    }, [modules, activeLessonId]);

    const updateLessonLocally = (updated) => {
        setModules((prev) => prev.map((m) => ({
            ...m,
            lessons: m.lessons.map((l) => (l.id === updated.id ? { ...l, ...updated } : l)),
        })));
    };

    const addModule = async () => {
        try {
            await apiCreateModule(courseId);
            await fetchModules();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to add module');
        }
    };

    const addLesson = async (moduleId) => {
        try {
            await apiCreateLesson(moduleId);
            await fetchModules();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to add lesson');
        }
    };

    const handleUpdateCourse = async () => {
        setSaving(true);
        setError(null);
        try {
            const lessonsToUpdate = modules.flatMap((m) => m.lessons || []);
            await Promise.all(
                lessonsToUpdate.map((lesson) =>
                    apiUpdateLesson(lesson.id, {
                        title: lesson.title,
                        content: lesson.content,
                        type: lesson.type,
                        resourceUrl: lesson.resourceUrl,
                        isPublished: lesson.isPublished,
                    })
                )
            );
            await fetchModules();
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to update course content');
        } finally {
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <div className="loading-enterprise">
                <div className="spinner-enterprise"></div>
                <span>Loading content…</span>
            </div>
        );
    }

    return (
        <div className="row g-4">
            {error && (
                <div className="col-12">
                    <div className="alert-enterprise alert-enterprise-danger">{error}</div>
                </div>
            )}
            <div className="col-lg-4 mb-3 mb-lg-0">
                <LessonsTree
                    modules={modules}
                    activeLessonId={activeLessonId}
                    onSelectLesson={setActiveLessonId}
                    onAddModule={addModule}
                    onAddLesson={addLesson}
                />
            </div>
            <div className="col-lg-8">
                <LessonEditor lesson={activeLesson} onChange={updateLessonLocally} />
            </div>
            <div className="col-12 d-flex justify-content-end mt-3">
                <button
                    type="button"
                    className="btn-enterprise-primary"
                    onClick={handleUpdateCourse}
                    disabled={saving}
                >
                    {saving ? 'Saving…' : 'Update Course'}
                </button>
            </div>
        </div>
    );
}
